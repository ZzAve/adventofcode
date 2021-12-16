package nl.zzave.adventofcode.twentytwentyone

import nl.zzave.adventofcode.twentytwentyone.Day16.Packet.OperatorInfo
import nl.zzave.adventofcode.twentytwentyone.Day16.Packet.Type.LITERAL
import nl.zzave.adventofcode.twentytwentyone.Day16.Packet.Type.OPERATOR

object Day16 : TwentyTwentyOneProblem<Long> {
    override var debugMode: Boolean = true

    // "A".toInt(16)
    override fun solvePart1(input: List<String>): Long {
        val binaryString = input.toBinaryString()

        logln("input: $input")
        logln("Binary string: $binaryString")

        val motherPackage = solve(binaryString)

        logln("===")
        logln("input: $input")
        motherPackage.prettyPrint()
        val sumOfVersions = motherPackage.sumOfVersions()
        logln("Sum of versions: $sumOfVersions")
        logln("===")
        return sumOfVersions
    }

    private fun solve(binaryString: String): Packet {
        val startId = 0
        val motherPackage = Packet(
            version = 0,
            type = OPERATOR,
            typeId = -1,
            startIndexSubPackets = startId,
            startIndex = startId,
            operatorInfo = OperatorInfo(subPacketSize = 1)

        )

        recurseHierarchy(startId, binaryString, motherPackage)
        return motherPackage
    }

    private fun recurseHierarchy(startId: Int, binaryString: String, motherPacket: Packet, depth: Int = 0) {
        debugln(depth, "Processing from $startId, ${binaryString.substring(startId)} (Inside: $motherPacket)")
        // Start
        var runningIndex = startId

        val version = binaryString.substring(runningIndex, runningIndex + 3).toInt(2)
        val typeId = binaryString.substring(runningIndex + 3, runningIndex + 6).toInt(2)
        val type = typeId.type()
        runningIndex += 6

        when (type) {
            LITERAL -> {
                // take 5 bits, until 1 bit is 0
                var literalValue = ""
                runningIndex -= 5 // for compensation of first entry
                do {
                    runningIndex += 5
                    literalValue += binaryString.substring(runningIndex + 1, runningIndex + 5)
                } while (binaryString.substring(runningIndex, runningIndex + 1) == "1")
                runningIndex += 5

                val maxIndex = motherPacket.operatorInfo
                    ?.subPacketBitLength
                    ?.let { it + motherPacket.startIndexSubPackets }
                    ?: Int.MAX_VALUE

                runningIndex = minOf(runningIndex, maxIndex, binaryString.length)

                val packet = Packet(
                    version = version,
                    type = type,
                    typeId = typeId,
                    value = literalValue.toLong(2),
                    startIndexSubPackets = runningIndex,
                    startIndex = startId
                )

                motherPacket.subPackets.add(packet)

            }
            OPERATOR -> {
                val lengthTypeId = binaryString.substring(runningIndex, runningIndex + 1)
                runningIndex += 1
                when (lengthTypeId) {
                    "0" -> {
                        val totalLengthOfSubPackets = binaryString.substring(runningIndex, runningIndex + 15).toInt(2)
                        runningIndex += 15

                        val packet = Packet(
                            version = version,
                            type = type,
                            typeId = typeId,
                            operatorInfo = OperatorInfo(subPacketBitLength = totalLengthOfSubPackets),
                            startIndexSubPackets = runningIndex,
                            startIndex = startId
                        )
                        motherPacket.subPackets.add(packet)


                    }
                    "1" -> {
                        val numberOfSubPacketsContained =
                            binaryString.substring(runningIndex, runningIndex + 11).toInt(2)
                        runningIndex += 11


                        val packet = Packet(
                            version,
                            type,
                            typeId = typeId,
                            operatorInfo = OperatorInfo(subPacketSize = numberOfSubPacketsContained),
                            startIndexSubPackets = runningIndex,
                            startIndex = startId
                        )
                        motherPacket.subPackets.add(packet)


                    }
                    else -> error("mehh")
                }
            }
        }
        debugln(depth, "Found $type packet starting at $startId to: ${motherPacket.subPackets.last()}")
        debugln(depth, "Updated ${motherPacket.type} packet starting at ${motherPacket.startIndex} to: $motherPacket")
        logln(
            "Progress: $runningIndex / ${binaryString.length}\t" + "=".repeat(((runningIndex * 1.0 / binaryString.length) * 50).toInt())
        )


        if (shouldProcessChild(motherPacket.subPackets.last())) {
            debugln(
                depth,
                "Should process child of subPacket at ${motherPacket.startIndex}. Specifically child: ${motherPacket.subPackets.last()}"
            )
            recurseHierarchy(runningIndex, binaryString, motherPacket.subPackets.last(), depth + 1)
        }
        runningIndex = motherPacket.startIndexSubPackets + motherPacket.subPackets.sumOf { it.packetBitLength() }
        if (shouldProcessSibling(motherPacket)) {
            debugln(
                depth,
                "Should process siblings of subPackets of packet at ${motherPacket.startIndex}. $motherPacket"
            )
            recurseHierarchy(runningIndex, binaryString, motherPacket, depth)
            debugln(depth, "(Found sibling for subPackets of $motherPacket)")
        }

        debugln(depth, "(done with packet starting at $startId)")
        debugln("")
        if (debugMode) motherPacket.prettyPrint(depth)

    }

    private fun shouldProcessChild(packet: Packet): Boolean {
        return packet.type == OPERATOR
                && (packet.subPackets.size < (packet.operatorInfo?.subPacketSize ?: Int.MAX_VALUE))
                && (packet.subPackets.sumOf { it.packetBitLength() } < (packet.operatorInfo?.subPacketBitLength
            ?: Int.MAX_VALUE))
    }

    private fun shouldProcessSibling(packet: Packet): Boolean {
        return packet.subPackets.size < (packet.operatorInfo?.subPacketSize ?: Int.MAX_VALUE)
                && packet.subPackets.sumOf { it.packetBitLength() } < (packet.operatorInfo?.subPacketBitLength
            ?: Int.MAX_VALUE)

    }


    private fun Int.type(): Packet.Type {
        return when (this) {
            4 -> LITERAL
            else -> OPERATOR
        }
    }

    override fun solvePart2(input: List<String>): Long {
        val binaryString = input.toBinaryString()

        logln("input: $input")
        logln("Binary string: $binaryString")

        val motherPackage = solve(binaryString)

        return motherPackage.expression()
    }

    data class Packet(
        val version: Int,
        val type: Type,
        val typeId: Int,
        val value: Long? = null,
        val subPackets: MutableList<Packet> = mutableListOf(),
        val startIndex: Int,
        var startIndexSubPackets: Int,
        val operatorInfo: OperatorInfo? = null,
    ) {
        fun sumOfVersions(): Long {
            return version + subPackets.sumOf { it.sumOfVersions() }
        }

        fun expression(): Long = when (typeId) {
            4 -> value ?: error("TypeId 4 should be a LITERAL 😱")
            0 -> subPackets.sumOf { it.expression() }
            1 -> subPackets.fold(1L) { acc, cur -> acc * cur.expression() }
            2 -> subPackets.minOf { it.expression() }
            3 -> subPackets.maxOf { it.expression() }
            5 -> if (subPackets[0].expression() > subPackets[1].expression()) 1 else 0
            6 -> if (subPackets[0].expression() < subPackets[1].expression()) 1 else 0
            7 -> if (subPackets[0].expression() == subPackets[1].expression()) 1 else 0
            else -> subPackets.also { check(it.size == 1) }[0].expression()
        }

        fun prettyPrint(depth: Int = 0) {
            print("\t".repeat(depth) + "Packet[")
            print("version=$version, type=$type, value=$value, startIndex:$startIndex)")
            if (subPackets.size > 0) {
                println(", subPackets: [")
                subPackets.forEach { it.prettyPrint(depth + 1) }
                println("\t".repeat(depth) + "]")
            } else {
                println(", subPackets: [] ]")
            }
        }

        fun packetBitLength(): Int {
            return (startIndexSubPackets - startIndex) + subPackets.sumOf { it.packetBitLength() }

        }

        enum class Type {
            LITERAL,
            OPERATOR
        }

        data class OperatorInfo(
            val subPacketBitLength: Int? = null,
            val subPacketSize: Int? = null
        )

    }
}

private fun List<String>.toBinaryString(): String {
    check(this.size == 1)
    return this[0].toCharArray().map {
        it
            .digitToInt(16)
            .toString(2)
            .padStart(4, '0')
    }.joinToString("")


}

fun main() {
    Day16.testSolution(listOf("38006F45291200"), 9)
    Day16.testSolution(listOf("EE00D40C823060"), 14)
    Day16.testSolution(listOf("8A004A801A8002F478"), 16)
    Day16.testSolution(listOf("620080001611562C8802118E34"), 12)
    Day16.testSolution(listOf("A0016C880162017C3686B18A3D4780"), 31)


    Day16.testSolution(listOf("C200B40A82"), expectedResultPart2 = 3)
    Day16.testSolution(listOf("04005AC33890"), expectedResultPart2 = 54)
    Day16.testSolution(listOf("880086C3E88112"), expectedResultPart2 = 7)
    Day16.testSolution(listOf("9C0141080250320F1802104A08"), expectedResultPart2 = 1)


    println("--------- NOW FOR REALS --------")
    Day16.runSolution("day16.data")
}
