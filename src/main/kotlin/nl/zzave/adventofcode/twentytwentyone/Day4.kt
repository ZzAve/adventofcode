package nl.zzave.adventofcode.twentytwentyone

import nl.zzave.adventofcode.printMatrix
import nl.zzave.adventofcode.transposeMatrix

object Day4 : TwentyTwentyOneProblem<Int> {
    override var debugMode: Boolean = false

    override fun solvePart1(input: List<String>): Int {
        val (numbersToCall, bingoBoards) = input.toDomainModel()
        bingoBoards.forEach { it.prettyPrint() }

        val (winningBoard, calledNumbers) = callNumbersUntilBingo(numbersToCall, bingoBoards)
        return winningBoard?.calculateScore(calledNumbers) ?: -1
    }

    private fun callNumbersUntilBingo(
        numbersToCall: List<Int>,
        bingoBoards: List<BingoBoard>
    ): Pair<BingoBoard?, List<Int>> = numbersToCall
        .runningFold(emptyList<Int>()) { calledNumbers, newNumber -> calledNumbers + listOf(newNumber) }
        .asSequence()
        .drop(1)
        .map { calledNumbersSoFar ->
            debugln("Processing ${calledNumbersSoFar.last()} ($calledNumbersSoFar)")
            bingoBoards.firstOrNull { it.hasBingo(calledNumbersSoFar) } to calledNumbersSoFar
        }
        .firstOrNull { it.first != null }
        ?: (null to numbersToCall)

    override fun solvePart2(input: List<String>): Int {
        val (numbersToCall, bingoBoards) = input.toDomainModel()
//        bingoBoards.forEach { it.prettyPrint() }

        val (losingBoard, calledNumbers) = callNumbersReverseUntilNoBingo(numbersToCall, bingoBoards)
        return losingBoard?.calculateScore(calledNumbers) ?: -1
    }

    private fun callNumbersReverseUntilNoBingo(
        numbersToCall: List<Int>,
        bingoBoards: List<BingoBoard>
    ): Pair<BingoBoard?, List<Int>> {
        return numbersToCall.asReversed()
            .runningFold(numbersToCall) { calledNumbers, newNumber -> calledNumbers - listOf(newNumber) }
            .asSequence()
            .map { calledNumbersSoFar ->
                debugln("Processing ${calledNumbersSoFar[calledNumbersSoFar.size - 2]} ($calledNumbersSoFar)")
                bingoBoards.firstOrNull { !it.hasBingo(calledNumbersSoFar - listOf(calledNumbersSoFar.last())) } to calledNumbersSoFar
            }
            .firstOrNull { it.first != null }
            ?: (null to numbersToCall)
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
            this.first().split(",").map { it.toInt() }.also { logln("Numbers to call: $it") }

        val bingoBoards: List<BingoBoard> = this.drop(1).windowed(size = 6, step = 6) // 1 emptyline, 5 board lines
            .map { list ->
                debugln(list)
                val map: List<List<Int>> = list.drop(1)
                    .map { line ->
                        line.trim().split("\\s+".toRegex())
                            .also { debugln(it) }
                            .map { it.toInt() }
                    }
                BingoBoard(map, transposeMatrix(map, 0))

            }


        return calledNumbers to bingoBoards
    }


    data class BingoBoard(
        val board: List<List<Int>>,
        val transposedBoard: List<List<Int>>
    ) {

        fun hasBingo(calledNumbers: List<Int>): Boolean {
            val aRowHasBingo: Boolean = board.fold(false) { acc, row ->
                acc || calledNumbers.containsAll(row).also { debugln("Row $row makes bingo?\t$it ") }
            }

            return aRowHasBingo || transposedBoard.fold(false) { acc, col ->
                acc || calledNumbers.containsAll(col).also { debugln("Column $col makes bingo?\t$it") }
            }
        }

        fun calculateScore(calledNumbers: List<Int>): Int {
            val unmarkedNumbers =
                board.fold(0) { total, row -> total + row.filter { !calledNumbers.contains(it) }.sum() }
            logln("Sum of unmarked numbers: $unmarkedNumbers")
            logln("Last number called: ${calledNumbers.last()}")
            return unmarkedNumbers * calledNumbers.last()
        }

        fun prettyPrint() {
            logln("--- BingoBoard ---")
            printMatrix(this.board)
            logln("------------------")
        }
    }
}

fun main() {
    Day4.runSolution("day4.data")
}



