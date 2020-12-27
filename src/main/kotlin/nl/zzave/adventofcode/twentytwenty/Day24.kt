package nl.zzave.adventofcode.twentytwenty

import nl.zzave.adventofcode.utils.Point2D
import kotlin.math.*

object Day24 {
    private val tileDirectionsInput = getTwentyTwentyFile("day24.data")

    private val directNeighbours = setOf(
        0.toNeighbourPoint(),
        60.toNeighbourPoint(),
        120.toNeighbourPoint(),
        180.toNeighbourPoint(),
        240.toNeighbourPoint(),
        300.toNeighbourPoint(),
    )

    fun solve(tileDirectionsInput: List<String> = Day24.tileDirectionsInput): Pair<Int, Int> {

        val tileDirections = parseTileDirectionsInput(tileDirectionsInput)
        println()

        val flippedTiles = flipTiles(tileDirections)
        println("nr of flipped tiles (black): ${flippedTiles.size}")


        printTiles(flippedTiles)


        val flippedTilesAfterGame = playGameOfTiles(flippedTiles, 100)

        println("final tiles flipped: ${flippedTilesAfterGame.size}")
        return flippedTiles.size to flippedTilesAfterGame.size
    }

    private tailrec fun playGameOfTiles(flippedTiles: Set<Point2D>, daysToSimulate: Int): Set<Point2D> {
        if (daysToSimulate < 1) return flippedTiles


        val interestingTiles = mutableSetOf<Point2D>()
        // figure out which tiles should be considered to flip
        // a tile is relevant if:
        // - tile is black (so in the set)
        // - or a direct neighbour of a black tile

        flippedTiles.forEach { tile ->
            interestingTiles.add(tile)
            // add all 6 neighbours
            directNeighbours.forEach { neighbour ->
                val element = tile + neighbour
                interestingTiles.add(element)
            }
        }

//        println("interesting tiles")
//        printTiles(interestingTiles, flippedTiles)
//        println("---")

        // apply rules to each relevant tile
        val newFlippedTiles = mutableSetOf<Point2D>()
        for (tile in interestingTiles) {
            val adjacentBlackTiles = flippedTiles.countAdjacentBlackTiles(tile)
            if (flippedTiles.contains(tile)) {
                // black tile
//                println("$tile is a black tile")
                if (adjacentBlackTiles == 1 || adjacentBlackTiles == 2) {
                    // stays a black tile
                    newFlippedTiles.add(tile)
                }
            } else {
                // white tile
                if (adjacentBlackTiles == 2) {
                    newFlippedTiles.add(tile)
                }
            }
        }

        printTiles(newFlippedTiles)
        println("Days left: $daysToSimulate: ${newFlippedTiles.size}")
        return playGameOfTiles(newFlippedTiles, daysToSimulate - 1)

    }

    private fun Point2D.gridded(decimals: Int)= Point2D(
        this.x / cos(toRadians(60)),
        this.y / sin(toRadians(60))

    )

    private fun printTiles(tiles: Set<Point2D>, highlightedTiles :Set<Point2D> = emptySet()) {
        val map = tiles.map { it.gridded(1) }.toSet()
        val highlighted = highlightedTiles.map { it.gridded(1) }.toSet()

        val min = map.fold(Point2D(0.0,0.0)) { acc, p ->
            Point2D(
                minOf(acc.x, p.x),
                minOf(acc.y, p.y),
            )
        }
        val max = map.fold(Point2D(0.0,0.0)) { acc, p ->
            Point2D(
                maxOf(acc.x, p.x),
                maxOf(acc.y, p.y),
            )
        }


        val colRange  = ((min.x).roundToInt())..(max.x).roundToInt()
        print("     \t")
        colRange.forEach { print("${abs(it % 10)}") }
        for (row in ((min.y).roundToInt())..(max.y ).roundToInt()) {
            println()
            val prow = "$row"
            print("$row${"".repeat(5-prow.length)}\t")
            for (col in colRange) {
//                if (row % 2 == 0 && col % 2 != 0 ) print(" ")
//                else if (row % 2 != 0 && col % 2 == 0 ) print(" ")
                when {
                    highlighted.contains(Point2D(col/1.0, row / 1.0)) -> print("0")
                    map.contains(Point2D(col/1.0, row / 1.0)) -> print("X")
                    else -> print(".")
                }
            }
        }
        println()
    }

    private fun Int.toNeighbourPoint(): Point2D {
        val rad = toRadians(this)
        return Point2D(cos(rad), sin(rad))

    }

    private fun flipTiles(tileDirections: List<List<Double>>): Set<Point2D> {
        val x = mutableSetOf<Point2D>()
        for (tilePath in tileDirections) {
            val finalLocation = tilePath.fold(Point2D(0.0, 0.0)) { acc, d ->
                acc + Point2D(cos(d), sin(d))
            }

            if (x.contains(finalLocation)) {
                x.remove(finalLocation)
            } else {
                x.add(finalLocation)
            }
        }

        return x
    }

    private operator fun Point2D.plus(move: Point2D): Point2D =
        Point2D(this.x + move.x, this.y + move.y)

    /**
     * Returns list of directions to go to for each tile, direction in radians (distance is always 1)
     */
    private fun parseTileDirectionsInput(tileDirectionsInput: List<String>): List<List<Double>> =
        tileDirectionsInput.map { entry ->
            parseTileDirectionInputLine(entry)
        }

    private fun parseTileDirectionInputLine(entry: String): MutableList<Double> {
        var i = 0
        val directions = mutableListOf<Double>()
        var nextDirection: String? = null
        while (i < entry.length) {
            val c = entry[i]
            if (c == 'n' || c == 's') {
                nextDirection = "$c"
            } else {
                if (nextDirection != null) {
                    nextDirection += c
                } else {
                    nextDirection = "$c"
                }

                // process next direction
                val degrees: Int = when (nextDirection) {
                    "e" -> 180
                    "se" -> -120
                    "sw" -> -60
                    "w" -> 0
                    "nw" -> 60
                    "ne" -> 120
                    else -> throw IllegalStateException("Couldn't process direction $nextDirection")
                }

                nextDirection = null
                directions.add(toRadians(degrees))
            }
            i++
        }
        return directions
    }

    private fun toRadians(degrees: Int): Double {
        return degrees * PI / 180.0
    }

    private fun Set<Point2D>.countAdjacentBlackTiles(tile: Point2D): Int {
        return directNeighbours.count {
            val element = tile + it
            this.contains(element)
        }
    }

}

fun main() {
    Day24.solve()
}