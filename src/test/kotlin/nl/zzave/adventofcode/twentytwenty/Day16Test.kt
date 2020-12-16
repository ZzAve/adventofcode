package nl.zzave.adventofcode.twentytwenty

import getFile
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class Day16Test {

    @Test
    fun x() {
        val myTicket = listOf("7,1,14")
        val nearbyTickets = listOf("7,3,47", "40,4,50", "55,2,20", "38,6,12")
        val rules = listOf(
            "class: 1-3 or 5-7",
            "row: 6-11 or 33-44",
            "seat: 13-40 or 45-50"
        )

        val (part1, part2) = Day16.solve(myTicket, nearbyTickets, rules)
        assertEquals(part1, 71)
    }

    @Test
    fun part2() {
        val myTicket = listOf("11,12,13")
        val nearbyTickets = listOf("3,9,18", "15,1,5", "5,14,9")

        val rules = listOf("class: 0-1 or 4-19", "row: 0-5 or 8-19", "seat: 0-13 or 16-19")

        val (part1, part2) = Day16.solve(myTicket, nearbyTickets, rules)
//        assertEquals(part2, -1)
    }

    @Test
    fun actual() {
        val myTicket = getFile("twentytwenty/day16_my_ticket.data")
        val nearbyTickets = getFile("twentytwenty/day16_nearby_tickets.data")
        val rules = getFile("twentytwenty/day16_rules.data")

        val (part1, part2) = Day16.solve(myTicket, nearbyTickets, rules)
        assertEquals(part1, 21071)
        assertEquals(part2, 3429967441937)
    }
}