package nl.zzave.adventofcode.twentytwentyone

import nl.zzave.adventofcode.twentytwentyone.Day5.debugMode
import nl.zzave.adventofcode.twentytwentyone.Day5.solvePart1
import nl.zzave.adventofcode.twentytwentyone.Day5.solvePart2
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class Day5Test {
    private fun getTestData() = getTestFile("Day5.data")

    @Test
    fun part1() {
        val input = getTestData()
        debugMode = true
        val result = solvePart1(input)

        assertEquals(-1, result)
    }

    @Test
    fun part2() {
        val input = getTestData()
        debugMode = true
        val result = solvePart2(input)

        assertEquals(-1, result)
    }
}
