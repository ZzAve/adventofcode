package nl.zzave.adventofcode.twentytwenty

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day23Test{
    @Test
    fun x(){
        val (part1, part2) = Day23.solve("389125467")

//        assertEquals("92658374", part1) //10
        assertEquals("67384529", part1) //100
        assertEquals(149245887792L, part2)
    }
}