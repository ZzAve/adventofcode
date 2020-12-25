package nl.zzave.adventofcode.twentytwenty

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day24Test{
    @Test
    fun x(){
        val testFile = getTestFile("day24.data")
        val (part1, part2) = Day24.solve(testFile)
        assertEquals(10, part1)
        assertEquals(2208, part2)
    }
}