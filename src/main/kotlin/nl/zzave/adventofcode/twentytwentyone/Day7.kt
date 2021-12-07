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
            val fuel = numbers.fold(0L) { acc, cur ->
                val distance = (cur - position).absoluteValue
                val cost = calculateCost(distance)
                debug("From $cur to $position costs $cost fuel")
                acc + cost
            }

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

    private val costs = mutableMapOf<Long, Long>()
    private fun calculateCost(distance: Long): Long =
        when {
            distance < 2 -> distance
            costs[distance] != null -> costs[distance]!!
            else -> costs.getOrElse(distance) {
                val cost = distance + calculateCost(distance - 1)
                costs[distance] = cost
                cost
            }
        }


}

fun main() {
    Day7.debugMode = true
    Day7.testSolution("day7-test.data", 37, 168)

    println("--------- NOW FOR REALS --------")

    Day7.debugMode = false
    Day7.runSolution("day7.data")
}
