package nl.zzave.adventofcode.twentytwentyone

import java.math.BigInteger

object Day14 : TwentyTwentyOneProblem<Long> {
    override var debugMode: Boolean = true

    override fun solvePart1(input: List<String>): Long {
        val (monomer, transformations) = parseInput(input)
        log("Monomer: $monomer")
        log("Allowed transformation: $transformations")

        val times = 10
        val elementsByOccurrence = breadthFirstSearch(monomer, transformations, times)

        log(elementsByOccurrence.toSortedMap())
        log(elementsByOccurrence.values.sorted())
        val maxOf = elementsByOccurrence.maxOf { it.value }
        val minOf = elementsByOccurrence.minOf { it.value }
        val toLong = (maxOf - elementsByOccurrence.values.sorted()[1]).toLong()
        log("Max: $maxOf - Min:$minOf = ${maxOf - minOf} (vs long: $toLong)")
        return (maxOf - minOf).toLong()
    }

    private fun breadthFirstSearch(
        monomer: List<Char>,
        transformations: Set<Transformation>,
        times: Int
    ): Map<Char, BigInteger> {

        var polymer = monomer

        repeat(times) {
            debug("On step ${it}:\t$polymer")

            polymer = listOf(polymer[0]) + polymer.map { it }.zipWithNext { a, b ->
                transformations.firstOrNull { it.left == a && it.right == b }
                    ?.let { listOf(it.result, b) }
                    ?: listOf(b)
            }.flatten()

            val tmp = polymer.groupBy { it }.mapValues { it.value.size }

            debug(tmp.toSortedMap())
            debug(tmp.values.sorted())

        }

        return polymer.groupBy { it }.mapValues { it.value.size.toBigInteger() }

    }

    override fun solvePart2(input: List<String>): Long {
        val (monomer, transformations) = parseInput(input)
        log("Monomer: $monomer")
        log("Allowed transformation: $transformations")

        // Given a pair, determine the result after 40 days.
        val occurrences: MutableMap<Char, BigInteger> = mutableMapOf(monomer.first() to BigInteger.ONE)
        monomer.zipWithNext { a, b -> calculateOccurrences(a to b, transformations, 40) }
            .forEach {
                it.forEach { (key, value) ->
                    occurrences[key] = (occurrences[key] ?: BigInteger.ZERO) + value
                }
            }

        log(occurrences.toSortedMap())
        log(occurrences.values.sorted())
        val maxOf = occurrences.maxOf { it.value }
        val minOf = occurrences.minOf { it.value }
        val toLong = (maxOf - minOf).toLong()
        log("Max: $maxOf - Min:$minOf = ${maxOf - minOf} (vs long: $toLong)")

        val pairOccurrences: MutableMap<Pair<Char, Char>, BigInteger> = mutableMapOf()
        monomer.zipWithNext { a, b -> calculatePairOccurrences(a to b, transformations, 40) }
            .forEach {
                it.forEach { (charPair, occur) ->
                    pairOccurrences[charPair] = (pairOccurrences[charPair] ?: BigInteger.ZERO) + occur
                }
            }

        log(pairOccurrences.toSortedMap { a, b -> a.first - b.first })
        // total occurences:
        val totalOccurrences = pairOccurrences
            .map { (key, value) -> key.second to value }
            .groupBy({ it.first }) { it.second }
            .mapValues { (_, value) -> value.sumOf { it } }
            .toMutableMap()

        // Add very first
        totalOccurrences[monomer.first()] =( totalOccurrences[monomer.first()]?: BigInteger.ZERO) + BigInteger.ONE

        log(totalOccurrences.toSortedMap())
        log(totalOccurrences.values.sorted())

        val max2 = totalOccurrences.maxOf { it.value }
        val min2 = totalOccurrences.minOf { it.value }

        log("Max2: $max2 - Min2:$min2 = ${max2 - min2}")




        return (maxOf - minOf).toLong()
    }

    private fun parseInput(input: List<String>): Pair<List<Char>, Set<Transformation>> {
        val monomer = input[0].trim().toCharArray().toList()

        val transformations = input.drop(2).map {
            val (pair, result) = it.split("->").map { t -> t.trim() }

            val (left, right) = pair.toCharArray()
            check(result.length == 1)
            Transformation(left, right, result[0])
        }
        return monomer to transformations.toSet()
    }

    private val pairCache = mutableMapOf<Pair<Pair<Char, Char>, Int>, Map<Pair<Char, Char>, BigInteger>>()
    private fun calculatePairOccurrences(
        pair: Pair<Char, Char>,
        transformations: Set<Transformation>,
        repeat: Int
    ): Map<Pair<Char, Char>, BigInteger> {
        val (firstChar, secondChar) = pair

        if (repeat == 0) return mapOf(pair to BigInteger.ONE)
        if (transformations.none { it.left == firstChar && it.right == secondChar }) return mapOf(pair to BigInteger.ONE)

        val cacheEntry = pairCache[pair to repeat]
        if (cacheEntry != null) return cacheEntry


//        debug("Finding combinations for $firstChar$secondChar ($repeat repetitions)")

        val occurrences = mutableMapOf<Pair<Char, Char>, BigInteger>()

        transformations.firstOrNull { it.left == firstChar && it.right == secondChar }
            ?.let { it ->
                val pair1 = it.left to it.result
                occurrences[pair1] = (occurrences[pair1] ?: BigInteger.ZERO) + BigInteger.ONE
                val pair2 = it.result to it.right
                occurrences[pair2] = (occurrences[pair2] ?: BigInteger.ZERO) + BigInteger.ONE
            }


        // find each pair in result
        val newOccurrences = mutableMapOf<Pair<Char,Char>,BigInteger>()
        occurrences.forEach { (key, value) ->
            calculatePairOccurrences(key, transformations, repeat - 1)
                .forEach { (charPair, occur) ->
                    newOccurrences[charPair] = (newOccurrences[charPair] ?: BigInteger.ZERO) + occur
                }
        }

        // save entry to cache
        pairCache[pair to repeat] = newOccurrences
        if(repeat%4==0) log("Pair $pair, repeat $repeat:\t$newOccurrences")
        else debug("Pair $pair, repeat $repeat:\t$newOccurrences")
        return newOccurrences
    }


    private val cache = mutableMapOf<Pair<Pair<Char, Char>, Int>, Map<Char, BigInteger>>()
    private fun calculateOccurrences(
        pair: Pair<Char, Char>,
        transformations: Set<Transformation>,
        repeat: Int
    ): Map<Char, BigInteger> {
        val (firstChar, secondChar) = pair

        if (repeat == 0) return mapOf(secondChar to BigInteger.ONE)
        val cacheEntry = cache[pair to repeat]
        if (cacheEntry != null) return cacheEntry

        val occurrences = mutableMapOf<Char, BigInteger>()

//        debug("Finding combinations for $firstChar-$secondChar ($repeat repetitions)")
        var subPolymer = listOf(firstChar, secondChar)

        subPolymer = listOf(subPolymer.first()) + subPolymer.map { it }.zipWithNext { a, b ->
            transformations.firstOrNull { it.left == a && it.right == b }
                ?.let { listOf(it.result, b) }
                ?: listOf(b)
        }.flatten()


        // find each pair in result
        subPolymer.zipWithNext { fst, snd -> calculateOccurrences(fst to snd, transformations, repeat - 1) }
            .forEach {
                it.forEach { (key, value) ->
                    occurrences[key] = (occurrences[key] ?: BigInteger.ZERO) + value
                }
            }

        // save entry to cache
        cache[pair to repeat] = occurrences

        debug("Pair $pair, repeat $repeat:\t$occurrences")
        return occurrences

    }

    data class Transformation(
        val left: Char,
        val right: Char,
        val result: Char
    ) {
        override fun toString(): String {
            return "$left$right -> $result"
        }
    }
}


fun main() {
    Day14.testSolution("day14-test.data", 1588, 2188189693529)
    println("--------- NOW FOR REALS --------")
    Day14.runSolution("day14.data")

}
