package nl.zzave.adventofcode.twentytwenty

import nl.zzave.adventofcode.splitByEmptyEntry
import nl.zzave.adventofcode.transposeMatrix
import kotlin.math.cos
import kotlin.math.sin

object Day20 {
    data class Tile(
        val id: Int,
        val points: MutableList<Point2D>,
        val uniqueSides: List<String>,
        val sideEncodings: MutableMap<Long, Side>,
        val hasAMatchWithAnotherUniqueSide: MutableMap<String, Boolean> = mutableMapOf(),
        var position: Point2D? = null //top left has 0,0 (so this basically gives the translation?) (only occurs in multiples of 10)
    ) {
        fun rotate(degrees: Int) {
            points.replaceAll { it.rotate(degrees) }
        }

        fun hasMatchingSides(other: Tile){

        }

    }

    enum class Side {
        TOP, RIGHT, BOTTOM, LEFT,
        FTOP, FRIGHT, FBOTTOM, FLEFT;

        fun flipped(): Side = when (this) {
            TOP -> FTOP
            FTOP -> TOP
            RIGHT -> FRIGHT
            FRIGHT -> RIGHT
            BOTTOM -> FBOTTOM
            FBOTTOM -> BOTTOM
            LEFT -> FLEFT
            FLEFT -> LEFT
        }
    }

    data class Point2D(
        val x: Double,
        val y: Double

    ) {

        fun rotate(degrees: Int): Point2D {
            val rad = degrees / 180.0 * Math.PI
            return Point2D(
                x * cos(rad) - y * sin(rad),
                x * sin(rad) + y * cos(rad)
            )

        }
    }

    private val tilesInput = getTwentyTwentyFile("day20.data")
    fun solve(tilesInput: List<String> = Day20.tilesInput): Pair<Long, Long> {

        // split by empty line
        val separateTiles = splitByEmptyEntry(tilesInput)
        val tiles = convertToTiles(separateTiles)

        println("All side encodings size: ${allSideEncodings.size}")
        println("All unique side encodings size: ${allSideEncodings.toSet().size}")
        determinePossibleNeighboursForTiles(tiles)

        //Corner pieces have only 2 matching sides
        val cornerPieces = tiles
            .filter { tile ->
                tile.hasAMatchWithAnotherUniqueSide
                    .filter { it.value }.size == 2
            }

        println("Found the corner pieces:")
        cornerPieces.forEach { println(it) }

        val productOfCornerPiecesIds = cornerPieces.fold(1L) { acc, tile -> acc * tile.id }
        println("Product of ids: $productOfCornerPiecesIds")


        // Side pieces have only 3 matching sides
        val sidePieces = tiles
            .filter { tile ->
                tile.hasAMatchWithAnotherUniqueSide
                    .filter { it.value }.size == 3
            }

        val centerPieces = tiles
            .filter { tile ->
                tile.hasAMatchWithAnotherUniqueSide
                    .filter { it.value }.size == 4
            }


        // start building 4 larger pieces, each starting with a corner pieces, and then expanding as we go.

        // always build towards a new square (2x2, 3x3, 4x4)


        // position cornerTile 0 in  the topleft corner
        cornerPieces[0].position = Point2D(0.0, 0.0)

        // determine orientation
        // non matching sides need to be left and up



        // put first index in place

        // check second index. If needed, flip


        // find relation between sides

        // rotate / flip accordingly


        return productOfCornerPiecesIds to -1L
    }

    private fun determineSide(tile: Tile, second: String) {
        TODO("Not yet implemented")
    }


    private fun determinePossibleNeighboursForTiles(tiles: List<Tile>) {
        for (tile in tiles) {
            if (tile.hasAMatchWithAnotherUniqueSide.filter { it.value }.size == 4) continue

            for (checkTile in tiles) {
                if (tile == checkTile) continue

                //check if tile has matches for all unique sides (allow reversion)
                checkSideTiles@ for (uniqueSideTile in tile.uniqueSides) {
                    val alreadyMatched = tile.hasAMatchWithAnotherUniqueSide[uniqueSideTile]
                    if (alreadyMatched == true) {
                        continue@checkSideTiles
                    }
                    for (uniqueSideCheckTile in checkTile.uniqueSides) {
                        if (uniqueSideTile == uniqueSideCheckTile || uniqueSideTile == uniqueSideCheckTile.reversed()) {
                            tile.hasAMatchWithAnotherUniqueSide[uniqueSideTile] = true
                            checkTile.hasAMatchWithAnotherUniqueSide[uniqueSideCheckTile] = true
                        }
                    }
                }
            }
        }
    }

    private val allSideEncodings = mutableListOf<Long>()
    private fun convertToTiles(separateTiles: List<List<String>>) =
        separateTiles.map { tile ->

            val id: Int = Regex(".*\\s(\\d{4}):").find(tile[0])
                ?.let { matchResult ->
                    matchResult.groups[1]?.value?.toInt()
                }
                ?: -1

            val subList = tile.subList(1, tile.size)
            val translation = Point2D(-(subList[1].length - 1) / 2.0, -(subList.size - 1) / 2.0)
            val points = mutableListOf<Point2D>()
            for (row in subList.indices) {
                for (col in subList[row].indices) {
                    if (subList[row][col] == '#') {
                        points.add(Point2D(col + translation.x, row + translation.y))
                    }
                }
            }

            val sideEncodings = mutableMapOf<Long, Side>()
            val uniqueSides = mutableListOf<String>()

            // row 0 and subList.size -1
            for ((row, side) in listOf(0, subList.size - 1).zip(listOf(Side.TOP, Side.BOTTOM))) {
                uniqueSides.add(subList[row])
                val fst = subList[row].toBinary()
                val snd = subList[row].reversed().toBinary()
                allSideEncodings.add(fst)
                allSideEncodings.add(snd)
                sideEncodings[fst] = side
                sideEncodings[snd] = side.flipped()

            }

            val transposed = transposeMatrix(subList.map { it.map { c -> c } }, '.', false)
            for ((row, side) in listOf(0, transposed.size - 1).zip(listOf(Side.LEFT, Side.RIGHT))) {
                uniqueSides.add(transposed[row].fold(""){acc,i -> acc+i})

                val fst = subList[row].toBinary()
                val snd = subList[row].reversed().toBinary()
                allSideEncodings.add(fst)
                allSideEncodings.add(snd)
                sideEncodings[fst] = side
                sideEncodings[snd] = side.flipped()
            }


            Tile(id, points, uniqueSides, sideEncodings)
        }


    private fun String.toBinary(char: Char = '#'): Long = map { if (it == char) "1" else "0" }.fold(""){acc,i -> acc+i}.toLong(2)
}

fun main() {

    Day20.solve()
}
