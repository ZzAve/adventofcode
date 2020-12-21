package nl.zzave.adventofcode.twentytwenty

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class Day21Test{

    @Test
    fun x(){
        val rawProducts = getTestFile("day21.data")
        val (part1, part2) = Day21.solve(rawProducts)

        assertEquals(5,part1)
        assertEquals("mxmxvkd,sqjhc,fvjkl",part2)
    }

}