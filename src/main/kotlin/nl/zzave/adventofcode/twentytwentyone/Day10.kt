package nl.zzave.adventofcode.twentytwentyone

import kotlin.math.floor

object Day10 : TwentyTwentyOneProblem<Long> {
    override var debugMode: Boolean = true

    override fun solvePart1(input: List<String>): Long {
        return input
            .map { it.toCharArray() }
            .mapNotNull { it.corruptedChar() }
            .also { debug("Found illegal chars: $it") }
            .sumOf {
                when (it) {
                    ')' -> 3L
                    '}' -> 1197L
                    ']' -> 57L
                    '>' -> 25137L
                    else -> error("illegal syntax exception")
                }
            }

    }

    override fun solvePart2(input: List<String>): Long {
        return input
            .map { it.toCharArray() }
            .filter { !it.isCorrupted() }
            .map { it.findCharArrayToComplete() }
            .map { chars ->
                chars.fold(0L) { acc, it ->
                    acc * 5 + when (it) {
                        ')' -> 1
                        ']' -> 2
                        '}' -> 3
                        '>' -> 4
                        else -> error("illegal syntax exception")
                    }
                }
            }
            .let { scores ->
                scores.sorted()[floor(scores.size / 2.0).toInt()]
            }


    }

    private val openChars = setOf('(', '{', '[', '<')
    private val closingChars = setOf(')', '}', ']', '>')
    private val closingMeetsOpen = mapOf(
        ')' to '(',
        '}' to '{',
        ']' to '[',
        '>' to '<'
    )
    private val openMeetsClosing = mapOf(
        '(' to ')',
        '{' to '}',
        '[' to ']',
        '<' to '>'
    )

    private fun CharArray.isCorrupted(): Boolean = corruptedChar() != null

    private fun CharArray.corruptedChar(): Char? {
        return if (!openChars.contains(this[0])) this[0]
        else {
            val list = fold(emptyList<Char>() to 'x') { acc, c ->
                when {
                    openChars.contains(c) -> acc.first + c to c
                    closingChars.contains(c) && acc.first.last() == closingMeetsOpen[c] -> {
                        val toMutableList = acc.first.toMutableList()
                        toMutableList.removeLast()
                        toMutableList to c
                    }
                    else -> return c
                }
            }

            //Don't check for incompletion
            null
        }
    }

    private fun CharArray.findCharArrayToComplete(): List<Char> {
        val list = fold(emptyList<Char>()) { acc, c ->
            when {
                openChars.contains(c) -> acc + c
                closingChars.contains(c) && acc.last() == closingMeetsOpen[c] -> {
                    val toMutableList = acc.toMutableList()
                    toMutableList.removeLast()
                    toMutableList
                }
                else -> error("syntax error")
            }
        }

        return list.reversed().map { openMeetsClosing[it] ?: error("syntax error") }

    }


}


fun main() {
    Day10.testSolution("day10-test.data", 26397, 288957)
    println("--------- NOW FOR REALS --------")
    Day10.runSolution("day10.data")
}
