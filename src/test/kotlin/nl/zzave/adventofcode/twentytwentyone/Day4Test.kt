package nl.zzave.adventofcode.twentytwentyone

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class Day4Test {
    @Test
    fun part1(){
        val input = getTestFile("day4.data")
        Day4.debugMode = true
        val result = Day4.solvePart1(input)

        assertEquals(4512, result)
    }

    @Test
    fun part2(){
        val input = getTestFile("day4.data")
        Day4.debugMode = true
        val result = Day4.solvePart2(input)

        assertEquals(1924, result)
    }
}
