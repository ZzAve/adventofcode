package nl.zzave.adventofcode.twentytwentyone

object Day14Try2 : TwentyTwentyOneProblem<Long> {
    override var debugMode: Boolean = true

    override fun solvePart1(input: List<String>): Long {
        val (monomer, transformations) = parseInput(input)
        logln("Monomer: $monomer")
        logln("Allowed transformation: $transformations")

        return solve(monomer, transformations, 10)

    }

    override fun solvePart2(input: List<String>): Long {
        val (monomer, transformations) = parseInput(input)
        logln("Monomer: $monomer")
        logln("Allowed transformation: $transformations")

        return solve(monomer, transformations, 40)
    }

    private fun solve(
        monomer: List<Char>,
        transformations: Set<Transformation>,
        times: Int
    ): Long {
        val startingOccurrenceMap: Map<Pair<Char, Char>, Long> = monomer.zipWithNext()
            .groupBy { it }
            .mapValues { it.value.size.toLong() }

        val finalOccurrenceMap: Map<Pair<Char, Char>, Long> = doStuff(startingOccurrenceMap, transformations, times)

        val xmap = mutableMapOf<Char, Long>()
        //Add very first
        xmap[monomer.first()] = 1
        finalOccurrenceMap.map { (key, value) ->
            xmap.computeIfAbsent(key.second) { 0L }
            xmap.computeIfPresent(key.second) { _, v -> v + value }
        }

        val mostOccurring = xmap.maxOf { it.value }
        val leastOccurring = xmap.minOf { it.value }

        return mostOccurring - leastOccurring;
    }

    private fun doStuff(
        occurrenceMap: Map<Pair<Char, Char>, Long>,
        transformations: Set<Transformation>,
        repetitions: Int
    ): Map<Pair<Char, Char>, Long> {

        var currentOccurrenceMap = occurrenceMap
        repeat(repetitions) {
            val newOccurrences = currentOccurrenceMap.map { (key, value) ->
                transformations.firstOrNull { it.left == key.first && it.right == key.second }
                    ?.let {
                        mapOf(
                            (it.left to it.result) to value,
                            (it.result to it.right) to value,
                        )
                    }
                    .let { it ?: mapOf(key to value) }

            }

            //merge new occurences
            val newOccurrenceMap = mutableMapOf<Pair<Char, Char>, Long>()
            newOccurrences.forEach { map ->
                map.forEach { (pair, occurrence) ->
                    newOccurrenceMap.computeIfAbsent(pair) { 0L }
                    newOccurrenceMap.computeIfPresent(pair) { _, v -> v + occurrence }
                }
            }

            currentOccurrenceMap = newOccurrenceMap
            debugln("occurrenceMap after $it iterations: $currentOccurrenceMap")
        }


        logln("Found occurrenceMap $currentOccurrenceMap")

        return currentOccurrenceMap
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
    Day14Try2.testSolution("day14-test.data", 1588, 2188189693529)
    println("--------- NOW FOR REALS --------")
    Day14Try2.runSolution("day14.data")

}
