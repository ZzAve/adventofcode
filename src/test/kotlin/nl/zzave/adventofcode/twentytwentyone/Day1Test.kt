package nl.zzave.adventofcode.twentytwentyone

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class Day1Test {
    @Test
    fun x(){
        val input = getTestFile("day1.data")
        val result = Day1.solvePart1(input)

        assertEquals(7, result)
    }

    @Test
    fun part2(){
        val input = getTestFile("day1.data")
        val result = Day1.solvePart2(input)

        assertEquals(5, result)
    }
}
