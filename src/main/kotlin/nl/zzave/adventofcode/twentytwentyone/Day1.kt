package nl.zzave.adventofcode.twentytwentyone

import java.lang.Integer.parseInt


object Day1 {


    fun solvePart1(input: List<String>): Int {
        val depthMeasurements: List<Int> = input.map { parseInt(it) }

        return countDeeperEntries(depthMeasurements)

    }

    /**
     * An entry (list item) is deeper if it is larger then the previous entry
     */
    private fun countDeeperEntries(depthMeasurements: List<Int>): Int = depthMeasurements
        .zipWithNext { a, b ->
            println("a $a vs b $b (= ${a < b})")
            a < b
        }
        .count { it }

    fun solvePart2(input: List<String>): Int {
        val depthMeasurements: List<Int> = input.map { parseInt(it) }
        println("Depth measurements: $depthMeasurements")

        return depthMeasurements
            .windowed(size = 4)
            .map { it.first() < it.last() }
            .also { println(it) }
            .count { it }
    }
}

fun main() {
    val input: List<String> = getTwentyTwentyOneFile("day1.data")
    val solvePart1 = Day1.solvePart1(input)
    println(solvePart1)

    val solvePart2 = Day1.solvePart2(input)
    println(solvePart2)
}
