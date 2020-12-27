package nl.zzave.adventofcode.twentytwenty

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day20Test{
    @Test
    fun x(){
        val testFile = getTestFile("day20.data")
        val (part1, part2) = Day20.solve(testFile)

        assertEquals(20899048083289L, part1)
        assertEquals(273, part2)


    }
}