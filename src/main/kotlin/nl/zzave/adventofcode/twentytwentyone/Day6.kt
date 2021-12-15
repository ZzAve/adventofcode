package nl.zzave.adventofcode.twentytwentyone

import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

object Day6 : TwentyTwentyOneProblem<Long> {
    override var debugMode: Boolean = false

    private const val RESET_DAYS = 6
    private const val NEW_DAYS = 8

    override fun solvePart1(input: List<String>): Long {
        val school = parseSchoolOfFish(input)
        logln("After 0 days: $school")

        val finalSchool = simulateDays(80, 80, school)
        debugln("After 80 days: $finalSchool")
        return finalSchool.values.sum()

    }

    override fun solvePart2(input: List<String>): Long {
        val school = parseSchoolOfFish(input)
        logln("After 0 days: $school")

        val finalSchool = simulateDays(256, 256, school)
        debugln("After 256 days: $finalSchool")
        return finalSchool.values.sum()
    }

    private fun parseSchoolOfFish(input: List<String>): MutableMap<Int, Long> {
        val school = mutableMapOf<Int, Long>()
        input.first().trim().split(",").forEach { fish ->
            val day = fish.toInt()
            school[day] = school[day]?.let { it + 1 } ?: 1
        }
        return school
    }

    private tailrec fun simulateDays(initialDays: Int, daysLeft: Int, schoolCounter: Map<Int, Long>): Map<Int, Long> {
        if (daysLeft == 0) {
            return schoolCounter
        }

        val newSchool = mutableMapOf<Int, Long>()
        schoolCounter.forEach { (days, count) ->
            when (days) {
                0 -> {
                    newSchool[RESET_DAYS] = newSchool.getOrDefault(RESET_DAYS, 0) + count
                    newSchool[NEW_DAYS] = count
                }
                else -> newSchool[days - 1] = newSchool.getOrDefault(days - 1, 0) + count
            }
        }

        debugln("After ${initialDays - (daysLeft - 1)} days (${newSchool.values.sum()} fish): $newSchool")
        return simulateDays(initialDays, daysLeft - 1, newSchool)
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
