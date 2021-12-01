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
    private fun countDeeperEntries(depthMeasurements: List<Int>): Int {
        return depthMeasurements.zipWithNext { a, b ->
            println("a $a vs b $b (= ${a < b})")
            if (a < b) 1 else 0
        }.sum()
    }

    fun solvePart2(input: List<String>): Int {
        val depthMeasurements: List<Int> = input.map { parseInt(it) }
        println(depthMeasurements)

        val depths = depthMeasurements.windowed(size = 3).map { it.sum() }

        println()
        println(depths)

        return countDeeperEntries(depths)

    }
}

fun main() {
    val input: List<String> = getTwentyTwentyOneFile("day1.data")
    val solvePart1 = Day1.solvePart1(input)
    println(solvePart1)

    val solvePart2 = Day1.solvePart2(input)
    println(solvePart2)
}
