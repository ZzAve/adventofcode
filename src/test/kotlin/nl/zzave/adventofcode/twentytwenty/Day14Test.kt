package nl.zzave.adventofcode.twentytwenty

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day14Test{
    @Test
    fun x(){

       val file= getFile("day14.data")
        val (part1, _) = Day14.solve(file)
        assertEquals(165, part1)


    }
    @Test
    fun y(){
        val file= getFile("day14_2.data")
        val (_, part2) = Day14.solve(file)
        assertEquals(208, part2)
    }
}