package nl.zzave.adventofcode.twentytwenty

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class Day15Test {

    @Test
    fun example() {
        val (part1, _) = Day15.solve(listOf(0,3,6))
        assertEquals(part1, 436)
    }

    @Test
    fun x() {
        val (part1, _) = Day15.solve(listOf(1, 3, 2))
        assertEquals(part1, 1)
    }

    @Test
    fun y() {
        val (part1, _) = Day15.solve(listOf(2,1,3))
        assertEquals(part1, 10)
    }

    @Test
    fun z() {
        val (part1, _) = Day15.solve(listOf(1,2,3))
        assertEquals(part1, 27)
    }



    @Test
    fun part2() {
        val (_, part2) = Day15.solve(listOf(0,3,6))
        assertEquals(part2, 175594)
    }

    @Test
    fun part2_2() {
        val (_, part2) = Day15.solve(listOf(1,3,2))
        assertEquals(part2, 2578)
    }

    @Test
    fun part2_3() {
        val (_, part2) = Day15.solve(listOf(2,1,3))
        assertEquals(part2, 3544142)
    }

}