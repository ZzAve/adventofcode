package nl.zzave.adventofcode.twentytwentyone

import kotlin.math.absoluteValue
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

object Day17 : TwentyTwentyOneProblem<Long> {
    override var debugMode: Boolean = true

    override fun solvePart1(input: List<String>): Long {
//        xPositionCache.clear()
//        velocityCache.clear()

        val targetArea = parseInput(input)
        logln("Finding best startVelocity to reach $targetArea")
        val (bestHeight, bestStartVelocity, successfulShots) = iterate(targetArea)
        logln("Found best startVelocity $bestStartVelocity")
        logln("Found best height ${-bestHeight}")
        logln("Found # of successful shots $successfulShots")

        return (-1*bestHeight).toLong()
    }

    private fun parseInput(input: List<String>): TargetArea {
        check(input.size == 1)
        val (_, xrange, yrange) = input[0].trim().split(": ", ", ")

        val (xmin, xmax) = xrange.trim().split("=")[1].split("..").map { it.toInt() }
        val (ymin, ymax) = yrange.trim().split("=")[1].split("..").map { it.toInt() }

        return TargetArea(xmin..xmax, ymin..ymax)
    }

    private fun gradientDescent(
        initialVelocity: Coord,
        targetArea: TargetArea,
        costFunction: (velocity: Coord, targetArea: TargetArea) -> Double
    ): Pair<Coord, Long> {
        var velocity = initialVelocity
        var alpha = 5
        var direction: Coord =
            Coord(1 * floor(sqrt(alpha.toDouble()) / 2).toInt(), 1 * floor(sqrt(alpha.toDouble()) / 2).toInt())
        var fitness: Double
        var bestVelocity: Coord = initialVelocity
        var bestFitness = costFunction(velocity, targetArea)
        var bestVelocityVisited = 1
        var i = 0
        while (alpha > 1 && bestVelocityVisited < 4) {
            velocity += direction
            fitness = costFunction(velocity, targetArea)
            // calulate gradient
            val xgrad = costFunction(velocity.copy(x = velocity.x + 1), targetArea) * 1.0 - fitness
            val ygrad = costFunction(velocity.copy(y = velocity.y + 1), targetArea) * 1.0 - fitness

            if (fitness < bestFitness) {
                bestFitness = fitness
                bestVelocity = velocity
                bestVelocityVisited = 1
            } else if (velocity == bestVelocity) {
                bestVelocityVisited += 1
            }

            if (i % 10 == 0) {
                alpha -= 1
            }

            val dx = xgrad / sqrt(xgrad.pow(2) + ygrad.pow(2))
            val dy = ygrad / sqrt(xgrad.pow(2) + ygrad.pow(2))

            direction = direction.copy(
                x = (dx * alpha).roundToInt(),
                y = (dy * alpha).roundToInt()
            )
            i++
            debugln("Iteration $i of gradient descent. Velocity:$velocity, direction:$direction, alpha:$alpha")
        }


        logln("Found $bestVelocity height: $bestFitness")
        return bestVelocity to bestFitness.toLong()
    }

    /**
     * returns the dist to plane in positive numbers
     * or the max height that is reached in negative numbers
     */
    private fun fitnessOfVelocity(startVelocity: Coord, targetArea: TargetArea, startT: Int): Double {
        var t = startT
        var v = 1
        val epsilon = 1 * 10.0.pow(-3)
        var bestFitness = dist(10, startVelocity, targetArea)
        var bestT = 10
        var fitness: Double
        var lastFitness = bestFitness
        var i = 0
        var bestTVisited = -1
        while (bestFitness > epsilon &&  i < bestTVisited + 100 ) {
            if (i % 100 == 0) debugln("$i iterations to find optimal t for $startVelocity. Best fitness $bestFitness (at t=$bestT. bestTVisited $bestTVisited) current t:$t ")
            t = maxOf(t + v, startT)
            if (t==startT) v = 1
            fitness = dist(t, startVelocity, targetArea)


            if (fitness < bestFitness) {
                bestFitness = fitness
                bestT = t
                bestTVisited = i
            }

            if (fitness <= lastFitness) {
                if (i < bestTVisited + 10) v += v.absoluteValue / v
//                v = minOf(5,v.absoluteValue) * v.absoluteValue/v
            } else {
                // flip
                v = -1 * v.absoluteValue/v  * maxOf(1, (v.absoluteValue*0.3).toInt())
                if (i > bestTVisited + 10) v= v.absoluteValue/v // reset it to 1
            }

            lastFitness = fitness
            i++
        }

        val finalScore = if (bestFitness > epsilon) {
            bestFitness
        } else {
            -(startVelocity.y * (startVelocity.y + 1) / 2.0)
        }
        debugln("Found fitness for $startVelocity: $finalScore (i: $i, targetArea $targetArea)")
        // calculateMaxHeight or set -Int_MAX_VALUE
        return finalScore
    }

    val distCache = mutableMapOf<Triple<Int,Coord, TargetArea>, Double>()
    private fun dist(t: Int, startVelocity: Coord, targetArea: TargetArea): Double {
        if (distCache[Triple(t , startVelocity , targetArea)] != null){
            return distCache[Triple(t , startVelocity , targetArea)]!!
        }

        val positionY = calculateYPosition(startVelocity.y, t)
        val positionX = calculateXPosition(startVelocity.x, t)
//        debugln("StartVelocity $startVelocity, position at t: $t -> ($positionX,$positionY)")

        val d = when {
            targetArea.x.first <= positionX && positionX <= targetArea.x.last &&
                    targetArea.y.first <= positionY && positionY <= targetArea.y.last -> {
                0.0
            }
            targetArea.x.first <= positionX && positionX <= targetArea.x.last -> {
                minOf(
                    (positionY - targetArea.y.first).absoluteValue,
                    (positionY - targetArea.y.last).absoluteValue
                ).pow(2)
            }
            targetArea.y.first <= positionX && positionX <= targetArea.y.last -> {
                minOf(
                    (positionX - targetArea.x.first).absoluteValue,
                    (positionX - targetArea.x.last).absoluteValue
                ).pow(2)
            }
            else -> {
                minOf(
                    (positionX - targetArea.x.first).pow(2) + (positionY - targetArea.y.first).pow(2),//top left
                    (positionX - targetArea.x.last).pow(2) + (positionY - targetArea.y.first).pow(2),//top right
                    (positionX - targetArea.x.last).pow(2) + (positionY - targetArea.y.last).pow(2),//bottom right
                    (positionX - targetArea.x.first).pow(2) + (positionY - targetArea.y.last).pow(2)  // bottom left
                )
            }
        }
        distCache[Triple(t , startVelocity , targetArea)] = d
        return d

    }

    private val yPositionCache = mutableMapOf<Pair<Int, Int>, Double>()
    private fun calculateYPosition(velocityAtT0: Int, t: Int): Double {
        return when {
            t == 0 -> 0.0
            yPositionCache[velocityAtT0 to t] != null -> {
                yPositionCache[velocityAtT0 to t]!!
            }
            else -> {
                val previousPosition = calculateYPosition(velocityAtT0, t - 1)
                val velocityAtTMinus1 = velocityAtT0 - (t - 1)
                val position = previousPosition + velocityAtTMinus1
                yPositionCache[velocityAtT0 to t] = position
                position
            }
        }
    }

    //velocityX, t to positionX
    private val xPositionCache = mutableMapOf<Pair<Int, Int>, Double>()
    private fun calculateXPosition(velocityAtT0: Int, t: Int): Double {
//        debugln("Calculating XPosition at t:$t for initialVelocity: $velocityAtT0")
        return when {
            t == 0 -> 0.0
            xPositionCache[velocityAtT0 to t] != null -> {
                xPositionCache[velocityAtT0 to t]!!
            }
            else -> {
                val previousPosition = calculateXPosition(velocityAtT0, t - 1)
                val velocityAtTMinus1 = calculateXVelocity(velocityAtT0, t - 1)
                val position = previousPosition + velocityAtTMinus1
                xPositionCache[velocityAtT0 to t] = position
                position
            }
        }
    }

    private val velocityCache = mutableMapOf<Pair<Int, Int>, Double>()
    private fun calculateXVelocity(velocityAtT0: Int, t: Int): Double =
        when {
            t == 0 -> velocityAtT0.toDouble()
            velocityCache[velocityAtT0 to t] != null -> {
                velocityCache[velocityAtT0 to t]!!
            }
            else -> {
                val previousVelocity = calculateXVelocity(velocityAtT0, t - 1)
                val value =
                    if (previousVelocity < 10.0.pow(-5)) 0.0 else previousVelocity.absoluteValue / previousVelocity
                val velocity = previousVelocity - value
                velocityCache[velocityAtT0 to t] = velocity
                velocity
            }
        }

    override fun solvePart2(input: List<String>): Long {
        //        xPositionCache.clear()
//        velocityCache.clear()

        val targetArea = parseInput(input)
        logln("Finding best startVelocity to reach $targetArea")

        val (bestHeight, bestStartVelocity, successfulShots) = iterate(targetArea)
        logln("Found best startVelocity $bestStartVelocity")
        logln("Found best height ${-bestHeight}")
        logln("Found # of successful shots $successfulShots")

//        logln("Found best startVelocity $velocity")
//        logln("Found best height $height")
        return successfulShots.toLong()

    }

    private fun iterate(targetArea: TargetArea): Triple<Double, Coord, Int> {
        var bestHeight = Double.MAX_VALUE
        var bestStartVelocity = Coord(-1, -1)
        val successFullShots = mutableListOf<Coord>()

        val minx = (1..targetArea.x.last).first { it * (it + 1) / 2 >= targetArea.x.first }
        logln("Iterating over x $minx..${targetArea.x.last}")
        val miny = targetArea.y.first
        logln("Iterating over y $miny..1000")
        for (x in minx..targetArea.x.last) {
            for (y in miny..1_000) {
                val startVelocity = Coord(x, y)
                val fitness = fitnessOfVelocity(startVelocity, targetArea, maxOf(0,y))
    //                val fitness2 = fitnessOfVelocity(startVelocity, targetArea,100)
    //                val fitness3 = fitnessOfVelocity(startVelocity, targetArea,1_000)
    //                val fitness4 = fitnessOfVelocity(startVelocity, targetArea,10_000)

                if (fitness <= 1.0*10.0.pow(-3)){
                    successFullShots.add(startVelocity)

                }
                if (minOf(fitness) < bestHeight) {
                    bestHeight = fitness
                    bestStartVelocity = startVelocity
                }


            }
        }

        logln(successFullShots)
        return Triple(bestHeight, bestStartVelocity, successFullShots.size)
    }

    data class TargetArea(
        val x: IntRange,
        val y: IntRange
    )
}

fun main() {
    Day17.testSolution("day17-test.data", 45, 112)
    println("--------- NOW FOR REALS --------")
    Day17.runSolution("day17.data")
}
