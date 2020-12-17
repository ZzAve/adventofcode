package day11

import getFile

object Day11 {
    private val initialSeatingArrangement = getFile("day11.data")
    fun solve() {
        val seatingArrangement = initialSeatingArrangement.map { it.toCharArray().toList() }
        val gameOfSeatsResult = playGameOfSeats(seatingArrangement, RuleSet.ADJACENCY)
//        printMatrix(gameOfSeatsResult)
        val occupiedSeats =
            gameOfSeatsResult.fold(0) { acc: Int, list -> acc + list.count { it == SeatOccupation.OCCUPIED.char } }
        println("Total number of occupied seats: $occupiedSeats")


        val gameOfSeatsPart2Result = playGameOfSeats(seatingArrangement, RuleSet.VISIBILITY)
//        printMatrix(gameOfSeatsPart2Result)
        val occupiedSeatsPart2 =
            gameOfSeatsPart2Result.fold(0) { acc: Int, list -> acc + list.count { it == SeatOccupation.OCCUPIED.char } }
        println("Total number of occupied seats after part2: $occupiedSeatsPart2")
    }


    enum class RuleSet {
        ADJACENCY,
        VISIBILITY
    }

    enum class SeatOccupation(val char: Char) {
        FLOOR('.'),
        EMPTY('L'),
        OCCUPIED('#');

        companion object {

            fun byChar(char: Char): SeatOccupation {
                return when (char) {
                    FLOOR.char -> FLOOR
                    EMPTY.char -> EMPTY
                    OCCUPIED.char -> OCCUPIED
                    else -> throw IllegalArgumentException("No seat type for char '$char'")
                }
            }
        }
    }

    private tailrec fun playGameOfSeats(seatingArrangment: List<List<Char>>, rules: RuleSet): List<List<Char>> {
        val newSeatingArrangement = mutableListOf<List<Char>>()
        var newRow: MutableList<Char>
        for (row in seatingArrangment.indices) {
            newRow = mutableListOf()
            for (seat in seatingArrangment[row].indices) {
                val newSeatOccupation =
                    when (rules) {
                        RuleSet.ADJACENCY -> applyAdjacencyRule(seatingArrangment, row, seat)
                        RuleSet.VISIBILITY -> applyVisibilityRule(seatingArrangment, row, seat)
                    }

                newRow.add(newSeatOccupation.char)
            }
            newSeatingArrangement.add(newRow)

        }

        return if (seatingArrangment == newSeatingArrangement) seatingArrangment
        else playGameOfSeats(newSeatingArrangement, rules)
    }

    private fun applyAdjacencyRule(
        seatingArrangement: List<List<Char>>,
        row: Int,
        seat: Int
    ): SeatOccupation {
        return when (SeatOccupation.byChar(seatingArrangement[row][seat])) {
            SeatOccupation.FLOOR -> SeatOccupation.FLOOR
            SeatOccupation.EMPTY -> {
                val adjecentOccupiedSeats = getAdjacentCharacterCount(
                    seatingArrangement,
                    row,
                    seat
                )
                if (adjecentOccupiedSeats == 0) SeatOccupation.OCCUPIED else SeatOccupation.EMPTY
            }
            SeatOccupation.OCCUPIED -> {
                val adjecentOccupiedSeats = getAdjacentCharacterCount(
                    seatingArrangement,
                    row,
                    seat
                )
                if (adjecentOccupiedSeats >= 4) SeatOccupation.EMPTY else SeatOccupation.OCCUPIED
            }
        }
    }


    fun getAdjacentCharacterCount(
        seatingArrangement: List<List<Char>>,
        mrow: Int,
        mseat: Int,
        adjacencyLimit: Int = 1,
        character: Char = '#',
        excludeMrowSeat: Boolean = true
    ): Int {
        var count = 0
        val rowRange = maxOf(0, (mrow - adjacencyLimit))..minOf(seatingArrangement.size - 1, mrow + adjacencyLimit)
        for (row in rowRange) {
            for (seat in maxOf(0, mseat - adjacencyLimit)..minOf(
                seatingArrangement[row].size - 1,
                mseat + adjacencyLimit
            )) {

                if (!(excludeMrowSeat && row == mrow && seat == mseat) &&
                    seatingArrangement[row][seat] == character
                ) {
                    count++
                }
            }
        }

        return count
    }

    private fun applyVisibilityRule(
        seatingArrangement: List<List<Char>>,
        row: Int,
        seat: Int
    ): SeatOccupation {
        return when (SeatOccupation.byChar(seatingArrangement[row][seat])) {
            SeatOccupation.FLOOR -> SeatOccupation.FLOOR
            SeatOccupation.EMPTY -> {
                val visibleOccupiedSeats = getVisibleOccupiedSeats(seatingArrangement, row, seat)
                if (visibleOccupiedSeats == 0) SeatOccupation.OCCUPIED else SeatOccupation.EMPTY
            }
            SeatOccupation.OCCUPIED -> {
                val visibleOccupiedSeats = getVisibleOccupiedSeats(seatingArrangement, row, seat)
                if (visibleOccupiedSeats >= 5) SeatOccupation.EMPTY else SeatOccupation.OCCUPIED
            }
        }
    }

    private val directions = listOf(

        -1 to -1, // move diagonally up and left
        -1 to 0,
        -1 to 1,
        0 to -1, // move vertically down
        0 to 1,  // move vertially up
        1 to -1,
        1 to 0,
        1 to 1  // move diagonally up and right

    )

    private fun getVisibleOccupiedSeats(seatingArrangement: List<List<Char>>, row: Int, seat: Int): Int {
        val occupiedSeatsVisible = directions.map { (rowDirection, seatDirection) ->
            isOccupiedSeatVisible(
                seatingArrangement,
                row + rowDirection,
                seat + seatDirection,
                rowDirection,
                seatDirection
            )
        }
        return occupiedSeatsVisible.count { it }
    }

    private fun isOccupiedSeatVisible(
        seatingArrangement: List<List<Char>>,
        row: Int,
        seat: Int,
        rowDirection: Int,
        seatDirection: Int
    ): Boolean {
        return when {
            row < 0 || row >= seatingArrangement.size -> false
            seat < 0 || seat >= seatingArrangement[row].size -> false
            seatingArrangement[row][seat] == SeatOccupation.EMPTY.char -> false
            seatingArrangement[row][seat] == SeatOccupation.OCCUPIED.char -> true
            seatingArrangement[row][seat] == SeatOccupation.FLOOR.char -> {
                isOccupiedSeatVisible(
                    seatingArrangement,
                    row + rowDirection,
                    seat + seatDirection,
                    rowDirection,
                    seatDirection
                )
            }
            else -> false // this doesn't happen?
        }
    }

}

fun main() {
    Day11.solve()
}