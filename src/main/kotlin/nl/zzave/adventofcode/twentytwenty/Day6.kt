package nl.zzave.adventofcode.twentytwenty

import nl.zzave.adventofcode.getFile
import nl.zzave.adventofcode.splitByEmptyEntry

object Day6 {
    val file = "day6.data"
    private val input = getFile(file)


    fun solve() {
        //find groups
        val groups = splitByEmptyEntry(input)

        val summedUniqueAnyYesses = groups.map {
            countUniqueAnyYesAnswers(it)
        }.sum()

        println("Sum of unique ANY yesses per group: $summedUniqueAnyYesses")

        val summedUniqueAllYesses = groups.map {
            countAllYesAnswers(it)
        }.sum()

        println("Sum of unique ALL yesses per group: $summedUniqueAllYesses")
    }




    private fun countUniqueAnyYesAnswers(groupInput: List<String>): Int =
        groupInput.reduce { acc: String, i -> acc + i }
            .let { it.filterIndexed { index, c -> it.indexOf(c) == index } }
//            .also { println("Group has ${it.length} ALL yesses ($it) ($groupInput)") }
            .length

    private fun countAllYesAnswers(groupInput: List<String>): Int =
        // for first answer sheet, check which answer comes up in ALL answer sheets
        groupInput[0]
            .filter { c ->
                groupInput.all {
                    it.any { c2 -> c2 == c }
                }
            }
//            .also { println("Group has ${it.length} ALL yesses ($it) ($groupInput)") }
            .length
}

fun main(args: Array<String>) {
    Day6.solve()
}
