package nl.zzave.adventofcode.twentytwentyone

import java.math.BigInteger

object Day14 : TwentyTwentyOneProblem<Long> {
    override var debugMode: Boolean = true

    override fun solvePart1(input: List<String>): Long {
        val (monomer, transformations) = parseInput(input)
        log("Monomer: $monomer")
        log("Allowed transformation: $transformations")

        var polymer = monomer
        repeat(10) {
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

        val elementsByOccurrence = polymer.groupBy { it }.mapValues { it.value.size }

        log(elementsByOccurrence.toSortedMap())
        log(elementsByOccurrence.values.sorted())
        return 0L + elementsByOccurrence.maxOf { it.value } - elementsByOccurrence.minOf { it.value }
    }

    override fun solvePart2(input: List<String>): Long {
        val (monomer, transformations) = parseInput(input)
        log("Monomer: $monomer")
        log("Allowed transformation: $transformations")

        // Given a pair, determine the result after 40 days.
        val occurences: MutableMap<Char, BigInteger> = mutableMapOf(monomer.first() to BigInteger.ONE)

        monomer.zipWithNext { a, b -> calculateOccurrences(a to b, transformations, 40) }
            .forEach {
                it.forEach { (key, value) ->
                    occurences[key] = (occurences[key] ?: BigInteger.ZERO) + value
                }
            }

        log(occurences.toSortedMap())
        log(occurences.values.sorted())
        val maxOf = occurences.maxOf { it.value }
        val minOf = occurences.minOf { it.value }
        val toLong = (maxOf - minOf).toLong()
        log("Max: $maxOf - Min:$minOf = ${maxOf-minOf} (vs long: $toLong")
        return toLong
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

    private val cache = mutableMapOf<Pair<Pair<Char,Char>, Int>,Map<Char,BigInteger>>()
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

        debug("Finding combinations for $firstChar-$secondChar ($repeat repetitions)")
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
