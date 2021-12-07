package nl.zzave.adventofcode.twentytwentyone

import kotlin.math.absoluteValue

object Day7 : TwentyTwentyOneProblem<Long> {
    override var debugMode: Boolean = false

    override fun solvePart1(input: List<String>): Long {
        val numbers = input.first().trim().split(",").map { it.toLong() }
        val min = numbers.reduce { acc, cur -> if (cur < acc) cur else acc }
        val max = numbers.reduce { acc, cur -> if (cur > acc) cur else acc }

        var currentMinFuel = Long.MAX_VALUE
        var currentMinFuelPosition = 0L

        for (position in min..max) {
            val fuel = numbers.fold(0L) { acc, cur -> acc + (cur - position).absoluteValue }
            debug("Position $position costs $fuel fuel")
            if (fuel < currentMinFuel) {
                debug("Best so far! (position $position, fuel costs: $fuel")
                currentMinFuel = fuel
                currentMinFuelPosition = position
            }
        }

        log("Found position: $currentMinFuelPosition")
        return currentMinFuel

    }

    override fun solvePart2(input: List<String>): Long {
        val numbers = input.first().trim().split(",").map { it.toLong() }

        var currentMinFuel = Long.MAX_VALUE
        var currentMinFuelPosition = 0L

        for (position in numbers.minOf { it }..numbers.maxOf { it }) {
            val fuel = calculateFuelCost(numbers, position, currentMinFuel)
            if (fuel < currentMinFuel) {
                debug("Best so far! (position $position, fuel costs: $fuel")
                currentMinFuel = fuel
                currentMinFuelPosition = position
            }
        }

        log("Found position: $currentMinFuelPosition")
        return currentMinFuel
    }

    private fun calculateFuelCost(numbers: List<Long>, position: Long, runningMin: Long): Long {
        var runningTotal = 0L
        for (number in numbers) {
            val distance = (number - position).absoluteValue
            val cost = distance * (distance + 1) / 2
            debug("From $number to $position costs $cost fuel")
            runningTotal += cost

            if (runningTotal > runningMin){
                // Early break
                return Long.MAX_VALUE
            }
        }

        return runningTotal
    }

    private fun calculateCost(distance: Long): Long = (distance * (distance + 1)) / 2
}

fun main() {
    Day7.debugMode = true
    Day7.testSolution("day7-test.data", 37, 168)

    println("--------- NOW FOR REALS --------")

    Day7.debugMode = false
    Day7.runSolution("day7.data")
}
