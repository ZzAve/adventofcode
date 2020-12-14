package nl.zzave.adventofcode.twentytwenty

import getFile
import kotlin.math.pow

object Day14 {
    private val data = getFile("twentytwenty/day14.data")

    fun solve(input: List<String> = data): Pair<Long, Long> {

        // map of index, value (36-bit, so long?)
        val memory1 = mutableMapOf<String, Long>()
        val memory2 = mutableMapOf<Long, Long>()
        var mask = "X".repeat(36)
        // go through input 1 by 1
        input.forEach {
            val split = it.split(" ")
            val operation = split[0]
            val value = split[2]
            if (operation == "mask") {
                mask = value
            } else {
                val (index, newValue) = applyMask1(operation, mask, value.toLong())
                memory1[index] = newValue
                val updatedAddresses = applyMask2(operation, mask)
                for (address in updatedAddresses) {
                    memory2[address] = value.toLong()
                }

            }
        }
        // if mask, update mask
        // if memory process entry
        val sum = memory1.values.sum()
        println("Sum of memory entries: $sum")


        val sum2 = memory2.values.sum()
        println("Sum of memory entries v2: $sum2")
        return sum to sum2
    }

    private fun applyMask2(operation: String, mask: String): List<Long> {
        val index = operation.substring(operation.indexOf("[") + 1, operation.indexOf("]")).toLong()

        // OR mask, 1 changes value into a 1, 0 leaves unchanged
        val orMask = mask.replace('X', '0')

        // make all X spots go to 0
        // AND mask, 0 changes value into a 0, 1 leaves unchanged
        val andMask = mask.replace('0', '1').replace('X', '0')

        val permutations = mutableListOf<Long>()
        mask.forEachIndexed { maskIndex, char ->
            if (char == 'X') {
                val pow: Long = 2.pow(35 - maskIndex) // 36 bitstring
                permutations.add(pow)
            }
        }

        val lowestValue = (index or orMask.toLong(2)) and andMask.toLong(2)
        return permutations(index, lowestValue, permutations)
    }

    private fun permutations(index: Long, lowestValue: Long, permutations: MutableList<Long>): List<Long> {
        if (permutations.size == 1) return listOf(lowestValue, lowestValue + permutations[0])

        val permutation = permutations[0]
        permutations.removeAt(0)

        val permutations1 = permutations(index, lowestValue, permutations)

        return permutations1.map {
            listOf(
                it,
                it + permutation
            )
        }
            .flatten()


    }

    private fun applyMask1(
        operation: String,
        mask: String,
        value: Long
    ): Pair<String, Long> {
        val index = operation.substring(operation.indexOf("[") + 1, operation.indexOf("]"))

        // OR mask, 1 changes value into a 1, 0 leaves unchanged
        val orMask = mask.replace('X', '0')
        val intermediateValue = value or orMask.toLong(2)

        // AND mask, 0 changes value into a 0, 1 leaves unchanged
        val andMask = mask.replace('X', '1')
        val newValue = intermediateValue and andMask.toLong(2)
        return index to newValue
    }


    private fun Int.pow(i: Int): Long {
        return this.toDouble().pow(i).toLong()
    }
}

fun main() {
    Day14.solve()
}