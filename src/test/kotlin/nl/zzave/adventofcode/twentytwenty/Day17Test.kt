package nl.zzave.adventofcode.twentytwenty

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class Day17Test {

    @Test
    fun x() {
        val startingPoint = listOf(
            ".#.",
            "..#",
            "###"
        )

        val (part1, part2) = Day17.solve(startingPoint)

        assertEquals(112, part1)
        assertEquals(848, part2)
    }
}