package nl.zzave.adventofcode.twentytwenty

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class Day13Test{

    @Test
    fun x(){
        val input = listOf("1","7,13,x,x,59,x,31,19")
        val (_, part2) = Day13.solve(input)

        assertEquals(1068781L, part2)
    }
    @Test
    fun y(){
        val input = listOf("1","17,x,13,19")
        val (_, part2) = Day13.solve(input)

        assertEquals(3417L, part2)
    }
}