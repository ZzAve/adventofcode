package nl.zzave.adventofcode.twentytwentyone

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class Day2Test {
    @Test
    fun part1(){
        val input = getTestFile("day2.data")
        val result = Day2.solvePart1(input)

        assertEquals(150, result)
    }

    @Test
    fun part2(){
        val input = getTestFile("day2.data")
        val result = Day2.solvePart2(input)

        assertEquals(900, result)
    }
}
