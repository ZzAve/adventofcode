package day6

import java.io.File

object Day6 {
    private val input = File("./src/main/resources/day6.data").readLines()

    fun solve() {
        //find groups
        val groups = findGroups(input)

        val summedUniqueAnyYesses = groups.map {
            countUniqueAnyYesAnswers(it)
        }.sum()

        println("Sum of unique ANY yesses per group: $summedUniqueAnyYesses")

        val summedUniqueAllYesses = groups.map {
            countAllYesAnswers(it)
        }.sum()

        println("Sum of unique ALL yesses per group: $summedUniqueAllYesses")
    }


    private fun findGroups(input: List<String>): List<List<String>> {
        val groups = mutableListOf<List<String>>()

        var currentGroup = mutableListOf<String>()
        input.forEach {
            if (it.isEmpty()) {
                groups.add(currentGroup)
                currentGroup = mutableListOf()
            } else {
                currentGroup.add(it)
            }
        }

        if (currentGroup.isNotEmpty()) {
            groups.add(currentGroup)
        }

        return groups

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
