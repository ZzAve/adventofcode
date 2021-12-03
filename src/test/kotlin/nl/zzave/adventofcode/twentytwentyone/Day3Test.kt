package nl.zzave.adventofcode.twentytwentyone

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class Day3Test {
    @Test
    fun part1(){
        val input = getTestFile("day3.data")
        val result = Day3.solvePart1(input)

        assertEquals(198, result)
    }

    @Test
    fun part2(){
        val input = getTestFile("day3.data")
        val result = Day3.solvePart2(input)

        assertEquals(230, result)
    }
}
