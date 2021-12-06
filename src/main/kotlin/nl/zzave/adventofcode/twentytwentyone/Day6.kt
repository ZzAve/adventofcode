package nl.zzave.adventofcode.twentytwentyone

import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

object Day6 : TwentyTwentyOneProblem<Long> {
    override var debugMode: Boolean = false

    override fun solvePart1(input: List<String>): Long {
        val school = input.first().trim().split(",").map { it.toInt() }
        log("After 0 days: $school")

        val finalSchool = processDays(80, 80, school)
        debug("After 80 days: $finalSchool")
        return finalSchool.count().toLong()

    }

    override fun solvePart2(input: List<String>): Long {
        val school = mutableMapOf<Int, Long>()
        input.first().trim().split(",").forEach { fish ->
            val day = fish.toInt()
            school[day] = school[day]?.let { it + 1 } ?: 1
        }
        log("After 0 days: $school")

        val finalSchool = processDays2(256, 256, school)
        debug("After 256 days: $finalSchool")
        return finalSchool.values.fold(0L) { acc, cur -> acc + cur }

    }

    private tailrec fun processDays(initialDays: Int, daysLeft: Int, school: List<Int>): List<Int> {
        if (daysLeft == 0) {
            return school
        }

        val newSchool = school.flatMap {
            when (it) {
                0 -> listOf(6, 8)
                else -> listOf(it - 1)
            }
        }

        debug("After ${initialDays - (daysLeft - 1)} days (${newSchool.count()} fish): $newSchool")
        return processDays(initialDays, daysLeft - 1, newSchool)

    }

    private tailrec fun processDays2(initialDays: Int, daysLeft: Int, schoolCounter: Map<Int, Long>): Map<Int, Long> {
        if (daysLeft == 0) {
            return schoolCounter
        }

        val newSchool = mutableMapOf<Int, Long>()
        schoolCounter.forEach { (days, count) ->
            when (days) {
                0 -> {
                    newSchool[6] = newSchool[6]?.let { it + count } ?: count
                    newSchool[8] = count
                }
                else -> newSchool[days - 1] = newSchool[days - 1]?.let { it + count } ?: count
            }
        }

        debug("After ${initialDays - (daysLeft - 1)} days (${newSchool.values.sum()} fish): $newSchool")
        return processDays2(initialDays, daysLeft - 1, newSchool)
    }
}

@OptIn(ExperimentalTime::class)
fun main() {
    measureTime {
        Day6.debugMode = true
        Day6.testSolution("day6-test.data", 5934L, 26984457539L)
    }.also {
        println("Took $it")
    }

    println("--------- NOW FOR REALS --------")

    measureTime {
        Day6.debugMode = false
        Day6.runSolution("day6.data")
    }.also {
        println("Took $it")
    }
}
