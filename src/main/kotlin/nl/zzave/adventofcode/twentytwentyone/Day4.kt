package nl.zzave.adventofcode.twentytwentyone

import nl.zzave.adventofcode.Problem
import nl.zzave.adventofcode.printMatrix
import nl.zzave.adventofcode.transposeMatrix

object Day4 : Problem<Int> {
    override var debugMode: Boolean = false


    override fun solvePart1(input: List<String>): Int {
        val (numberToCall, bingoBoards) = input.toDomainModel()
        bingoBoards.forEach { it.prettyPrint() }

        val (winningBoard, calledNumbers) = callNumbersUntilBingo(numberToCall, bingoBoards)
        return winningBoard?.calculateScore(calledNumbers) ?: -1
    }

    override fun solvePart2(input: List<String>): Int {
        val (numberToCall, bingoBoards) = input.toDomainModel()
//        bingoBoards.forEach { it.prettyPrint() }

        val (losingBoard, calledNumbers) = callNumbersReverseUntilNoBingo(numberToCall, bingoBoards)
        return losingBoard?.calculateScore(calledNumbers) ?: -1

    }

    private fun callNumbersReverseUntilNoBingo(
        numbersToCall: List<Int>,
        bingoBoards: List<BingoBoard>
    ): Pair<BingoBoard?, List<Int>> {
        val allNumbers = numbersToCall.asReversed().fold(numbersToCall.toMutableList()) { calledNumbers, lastNumber ->
            calledNumbers -= lastNumber
            bingoBoards.forEach {
                if (!it.hasBingo(calledNumbers)) {
                    calledNumbers += lastNumber // Add it again, as this is the magic Bingo number
                    return it to calledNumbers
                }
            }
            calledNumbers
        }

        return null to allNumbers
    }


    private fun callNumbersUntilBingo(
        numbersToCall: List<Int>,
        bingoBoards: List<BingoBoard>
    ): Pair<BingoBoard?, List<Int>> {
        val allNumbers = numbersToCall.fold(mutableListOf<Int>()) { calledNumbers, newNumber ->
            calledNumbers += newNumber
            bingoBoards.forEach {
                if (it.hasBingo(calledNumbers)) {
                    return it to calledNumbers
                }
            }
            calledNumbers
        }

        return null to allNumbers
    }


    data class BingoBoard(
        val board: List<List<Int>>,
        val transposedBoard: List<List<Int>>
    ) {

        fun prettyPrint() {
            log("--- BingoBoard ---")
            printMatrix(this.board)
            log("------------------")
        }

        fun hasBingo(calledNumbers: List<Int>): Boolean {
            // check rows (stop if number is missed, return if full row found)
            val aRowHasBingo: Boolean = board.fold(false) { acc, row ->
                acc || calledNumbers.containsAll(row).also { debug("Row $row makes bingo?\t$it ") }
            }

            // check columns (stop if number is missed, return if full row found)
            return aRowHasBingo || transposedBoard.fold(false) { acc, col ->
                acc || calledNumbers.containsAll(col).also { debug("Column $col makes bingo?\t$it") }
            }
        }

        fun calculateScore(calledNumbers: List<Int>): Int {
            val unmarkedNumbers =
                board.fold(0) { total, row -> total + row.filter { !calledNumbers.contains(it) }.sum() }
            log("Sum of unmarked numbers: $unmarkedNumbers")
            log("Last number called: ${calledNumbers.last()}")
            return unmarkedNumbers * calledNumbers.last()
        }
    }


    /**
     * @see twentytwentyone.day4.data for input data
     *
     * first line               -- called numbers
     * 2nd line                 -- white line
     * 3rd line till 7th line   -- bingo card
     * 8th line                 -- white line
     * 9th line till 14th line  -- bingo card
     * ...
     */
    private fun List<String>.toDomainModel(): Pair<List<Int>, List<BingoBoard>> {
        val calledNumbers: List<Int> =
            this.first().split(",").map { it.toInt() }.also { log("Numbers to call: $it") }

        val bingoBoards: List<BingoBoard> = this.drop(1).windowed(size = 6, step = 6) // 1 emptyline, 5 board lines
            .map { list ->
                debug(list)
                val map: List<List<Int>> = list.drop(1)
                    .map { line ->
                        line.trim().split("\\s+".toRegex())
                            .also { debug(it) }
                            .map { it.toInt() }
                    }
                BingoBoard(map, transposeMatrix(map, 0))

            }


        return calledNumbers to bingoBoards
    }
}

fun main() {
    Day4.runSolution("day4.data")
}



