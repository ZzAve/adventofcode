package nl.zzave.adventofcode.twentytwentyone

import kotlin.math.max

object Day21 : TwentyTwentyOneProblem<Long> {
    override var debugMode: Boolean = true

    override fun solvePart1(input: List<String>): Long {
        val startPositions = parseInput(input)


        val deterministicDie = DeterministicDie(100)
        val positions = startPositions.map { it }.toMutableList()
        val scores = startPositions.map { 0 }.toMutableList()
        var turn = 0
        while (scores.none { it >= 1000 }) {
            val roll = deterministicDie.roll(3)
            positions[turn % 2] = (positions[turn % 2] + roll) % 10
            scores[turn % 2] += positions[turn % 2] + 1

            debugln("Player ${(turn % 2) + 1} throws: $roll and moves to space ${positions[turn % 2]} for a total score of ${scores[turn % 2]}")
            turn++
        }

        val nrOfRolls = deterministicDie.rolled
        val loserScore = scores.minOf { it }

        return nrOfRolls * loserScore
    }

    private fun parseInput(input: List<String>): List<Int> {
        check(input.size == 2)
        return input.mapIndexed { index, it ->
            val (player, position) = it.split(":").map { a -> a.trim() }
            check(player.contains("Player ${index + 1}"))
            position.toInt() - 1
        }

    }

    override fun solvePart2(input: List<String>): Long {
        val startPositions = parseInput(input)


        val quantumDie = QuantumDie(3)
        val positions = startPositions.map { it }.toMutableList()
        val scores = startPositions.map { 0L }.toMutableList()
        val turn = 0

        val (player1Wins, player2Wins) = playDiracDice(positions, scores, quantumDie, turn)
        logln("Player 1 wins in $player1Wins universes")
        logln("Player 2 wins in $player2Wins universes")

        return max(player1Wins,player2Wins)

    }

    /**
     * Returns the winner: 0 (player 1) or 1 (player 2)
     */
    private fun playDiracDice(
        positions: List<Int>,
        scores: List<Long>,
        quantumDie: QuantumDie,
        turn: Int
    ): Pair<Long, Long> {
        if (quantumCache[CacheEntry(turn%2, positions, scores)] != null){
            return quantumCache[CacheEntry(turn%2, positions, scores)]!!
        }

        if (scores.any { it >= 21L }) {
            // we have a winner
            val winner = scores.indexOf(scores.minOf { it })
            return if (winner == 0) 1L to 0L else 0L to 1L
        }

        val rollResult = quantumDie.roll(3)
        val rollResults = rollResult.groupBy { it }.mapValues { (_, v) -> v.size }

        val winnersOfSubgame = rollResults.map { (roll, multiplier) ->
            val positions2: MutableList<Int> = positions.toMutableList()
            val scores2: MutableList<Long> = scores.toMutableList()
            positions2[turn % 2] = (positions2[turn % 2] + roll) % 10
            scores2[turn % 2] = scores2[turn % 2] + positions2[turn % 2] + 1

//            if (turn % 5 ==0) debugln("\t".repeat(turn)+"Player ${(turn % 2) + 1} throws: $roll and moves to space ${positions2[turn % 2]} for a total score of ${scores2[turn % 2]}")

            val diracDiceResult = playDiracDice(positions2, scores2, quantumDie, turn + 1)
            diracDiceResult * multiplier
        }


        val finalScores = winnersOfSubgame.sumOf { it.first } to winnersOfSubgame.sumOf { it.second }
        quantumCache[CacheEntry(turn%2, positions, scores)] = finalScores
        return finalScores

    }

    data class CacheEntry(
        val playerTurn: Int,
        val positions: List<Int>,
        val scores: List<Long>
    )
    private val quantumCache = mutableMapOf<CacheEntry, Pair<Long,Long>>()

    /**
     * Is zero based
     * Contains numbers 0 until sides (exclusive)
     */
    data class DeterministicDie(val sides: Int, var nextIndex: Int = 0, var rolled: Long = 0) {

        /**
         * Returns 1 based result 1..sides (inclusive
         */
        fun roll(times: Int): Int {
            var sumOfRolls = 0
            repeat(times) {
                sumOfRolls += nextIndex + 1
                nextIndex = (nextIndex + 1) % sides

                rolled++
            }

            return sumOfRolls

        }
    }

    data class QuantumDie(val sides: Int, var rolled: Long = 0) {

        fun roll(times: Int, sumOfRolls: List<Int> = listOf(0)): List<Int> {
            if (times == 0) return sumOfRolls
            val sumOfRollAfterRoll = sumOfRolls.flatMap {
                (1..sides).map { side ->
                    it + side
                }
            }

            rolled++



            return roll(times - 1, sumOfRollAfterRoll)

        }

        private fun singleRollResult(it: Int) = (1..sides).map { side ->
            it + side + 1
        }
    }
}

private operator fun Pair<Long, Long>.times(multiplier: Int): Pair<Long, Long> = this.first * multiplier to this.second * multiplier

fun main() {
    Day21.testSolution("day21-test.data", 739785, 444356092776315)
    println("--------- NOW FOR REALS --------")
    Day21.runSolution("day21.data")
}
