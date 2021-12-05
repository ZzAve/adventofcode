package nl.zzave.adventofcode.twentytwentyone

import nl.zzave.adventofcode.prettyPrint
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.absoluteValue
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

object Day5 : TwentyTwentyOneProblem<Int> {
    override var debugMode: Boolean = false

    override fun solvePart1(input: List<String>): Int {
        val grid = input.toDomain(false)

        if (debugMode) prettyPrint(grid)
        return grid.count { it.value > 1 }
    }

    override fun solvePart2(input: List<String>): Int {
        repeat(10) { debug(" v") }
        debug("----- PART 2 -- ")
        val grid = input.toDomain(allowDiagonal = true)
        return grid.count { it.value > 1 }

    }

    private fun List<String>.toDomain(allowDiagonal: Boolean = false): Map<Coord, Int> = mapNotNull {
        val (coord1, coord2) = parseLineDescription(it)

        coord1.expandTo(coord2, allowDiagonal)
            .also { expandedCoords -> debug("Expanding $coord1 to $coord2 to $expandedCoords ") }
    }.reduce { acc, line ->
        debug("Reducing $acc and $line")
        val mutableAcc = acc.toMutableMap()
        line.map { (key, value) ->
            mutableAcc[key] = mutableAcc[key]?.let { it + value } ?: 1
        }
        mutableAcc
    }

    private fun parseLineDescription(it: String) = it
        .split("->")
        .map {
            it.trim()
                .split(",")
                .let { c -> Coord(c[0].toInt(), c[1].toInt()) }
        }

    data class Coord(
        val x: Int,
        val y: Int
    ) {

        fun expandTo(that: Coord, allowDiagonal: Boolean = false): Map<Coord, Int> = when {
            this.y == that.y -> (min(this.x, that.x)..max(this.x, that.x)).map { Coord(it, y) }
            this.x == that.x -> (min(this.y, that.y)..max(this.y, that.y)).map { Coord(x, it) }
            allowDiagonal && (this.x - that.x).absoluteValue == (this.y - that.y).absoluteValue -> {
                val direction = (that - this).direction()
                val steps = (this.x - that.x).absoluteValue
                (0..steps).map {
                    Coord(
                        this.x + it * direction.x,
                        this.y + it * direction.y
                    )
                        .also { coord -> debug("Expanding $this to $that, found $coord") }
                }
            }
            else -> emptyList<Coord>().also { debug("Skipping expansion from $this to $that") }
        }.fold(mutableMapOf()) { acc, coord ->
            acc[coord] = acc[coord]?.let { it + 1 } ?: 1
            acc

        }

        private fun direction() = Coord(
            this.x.absoluteValue / this.x,
            this.y.absoluteValue / this.y
        )

        private operator fun minus(that: Coord): Coord = Coord(this.x - that.x, this.y - that.y)
    }
}

@OptIn(ExperimentalTime::class)
fun main() {
    measureTime {
        Day5.debugMode = true
        Day5.testSolution("day5-test.data", 5, 12)
        println("hi")
    }.also {
        println("Took ${it.inWholeMilliseconds}ms")
    }

    println("--------- NOW FOR REALS --------")

    measureTime {
        Day5.debugMode = false
        Day5.runSolution("day5.data")
    }.also {
        println("Took ${it.inWholeSeconds}s")
    }
}
