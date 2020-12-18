package nl.zzave.adventofcode.twentytwenty

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class Day18Test {
    @Test
    fun simplified() {
        val homework = listOf("1 + 2 * 3 + 4 * 5 + 6")
        val (part1, part2) = Day18.solve(homework)

        assertEquals(71, part1)
        assertEquals(231, part2)
    }

    @Test
    fun x() {
        val homework = listOf("1 + (2 * 3) + (4 * (5 + 6))")
        val (part1, part2) = Day18.solve(homework)

        assertEquals(51, part1)
        assertEquals(51, part2)
    }

    @Test
    fun y() {
        val homework = listOf(
            "2 * 3 + (4 * 5)",
            "5 + (8 * 3 + 9 + 3 * 4 * 3)",
            "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))",
            "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2"
        )
        val (part1, part2) = Day18.solve(homework)

        assertEquals(26 + 437 + 12240 + 13632, part1)
        assertEquals(
            46
                    +
                    1445
                    + 669060
                    + 23340, part2
        )
    }
}