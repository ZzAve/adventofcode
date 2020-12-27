package nl.zzave.adventofcode.twentytwenty

import nl.zzave.adventofcode.printMatrixOfStrings
import nl.zzave.adventofcode.splitByEmptyEntry
import nl.zzave.adventofcode.transposeMatrix
import nl.zzave.adventofcode.utils.Point2D
import nl.zzave.adventofcode.utils.Point2DInt
import kotlin.math.abs
import kotlin.math.roundToInt

object Day20 {
    data class Tile(
        val id: Int,
        val points: MutableList<Point2D>,
        val uniqueSides: List<String>,
        val sideEncodings: MutableMap<Long, Side>,
        val center: Point2D,
        val hasAMatchWithAnotherUniqueSide: MutableMap<String, Boolean> = mutableMapOf(),
        var position: Point2D? = null, //top left has 0,0 (so this basically gives the translation?) (only occurs in multiples of 10)
        val matches: MutableMap<Side, Pair<Side, Tile>> = mutableMapOf(),
        var processed: Boolean = false,
        var offset: Point2D = Point2D(0.0, 0.0),
        var orientation: Orientation = Orientation(0, false)
    ) {
        fun rotatedPoints(degrees: Int): List<Point2D> = rotatedPoints(degrees, false)

        fun rotatedPoints(degrees: Int, flippedHorizontally: Boolean): List<Point2D> =
            points.map {
                val translated = it - center
                var rotated = translated.rotated(degrees)
                if (flippedHorizontally) rotated = rotated.flippedHorizontally()
                val translatedBack = rotated + center
                translatedBack
            }

        override fun toString(): String {
            return "Tile[id: $id, sideEncodings: $sideEncodings, uniqueSides: $uniqueSides, matches: ${matches.map { "${it.key}" }}]"
        }
    }

    enum class Side(val unitVector: Point2D, val flipped: Boolean) {
        RIGHT(Point2D(1.0, 0.0), false),
        TOP(Point2D(0.0, 1.0), false),
        LEFT(Point2D(-1.0, 0.0), false),
        BOTTOM(Point2D(0.0, -1.0), false),
        FRIGHT(Point2D(1.0, 0.0), true),
        FTOP(Point2D(0.0, 1.0), true),
        FLEFT(Point2D(-1.0, 0.0), true),
        FBOTTOM(Point2D(0.0, -1.0), true);

        fun flipped(): Side = byDirection(this.unitVector, !this.flipped)

        fun rotate(degrees: Int): Side = byDirection(this.unitVector.rotated(degrees), this.flipped)

        fun rotateCounterClockWise(): Side = byDirection(this.unitVector.rotated(90), this.flipped)

        fun opposed(): Side = byDirection(this.unitVector.rotated(180), this.flipped)

        fun horizontalFlip(): Side = byDirection(
            Point2D(unitVector.x * -1, unitVector.y),
            !this.flipped
        )

        companion object {
            fun byDirection(unitVector: Point2D, flipped: Boolean): Side {
                return values().first { it.unitVector == unitVector && it.flipped == flipped }
            }
        }

    }


    data class Orientation(val rotation: Int, val flippedHorizontally: Boolean)

    private val tilesInput = getTwentyTwentyFile("day20.data")

    private val seaMonster = listOf(
        "..................#.",
        "#....##....##....###",
        ".#..#..#..#..#..#.."
    )

    fun solve(tilesInput: List<String> = Day20.tilesInput): Pair<Long, Int> {

        // split by empty line
        val separateTiles = splitByEmptyEntry(tilesInput)
        val tiles = convertToTiles(separateTiles)

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

        // Start composing total puzzle.
        // Pick one of the corners
        // put at 0,0 (BOTTOM LEFT)
        // ensure matching sides go right and down OR
        // ensure non matching sides go left and up

        val puzzle = mutableSetOf<Point2D>()
        val placedCornerPiece = placeACornerPiece(puzzle, cornerPieces)

        printPuzzle(puzzle)

        // Now, recursively apply all matches
        applyMatches(puzzle, placedCornerPiece)


        println("Final puzzle:")
        printPuzzle(puzzle)

        // remove borders
        val puzzleWithoutBorders = puzzle.mapNotNull {
            when {
                it.x.roundToInt() % 10 == 0 || it.x.roundToInt() % 10 == 9 -> null
                it.y.roundToInt() % 10 == 0 || it.y.roundToInt() % 10 == 9 -> null
                else -> Point2D(
                    it.x - (2 * (it.x.roundToInt() / 10) + 1),
                    it.y - (2 * (it.y.roundToInt() / 10) + 1),
                )
            }
        }

        printPuzzle(puzzleWithoutBorders, withGaps = false)

        // Now, find something
        val seaMonsterPoints = convertToPoints(seaMonster)

        val min = puzzleWithoutBorders.reduce { acc, p -> Point2D(minOf(acc.x, p.x), minOf(acc.y, p.y)) }
        val max = puzzleWithoutBorders.reduce { acc, p -> Point2D(maxOf(acc.x, p.x), maxOf(acc.y, p.y)) }

        val puzzleAsTile = Tile(
            0,
            puzzleWithoutBorders.toMutableList(),
            emptyList(),
            mutableMapOf(),
            Point2D((max.x + min.x) / 2, (max.y + min.y) / 2)
        )

        val allHits = mutableSetOf<Point2D>()
        val allHitCount = mutableListOf<Int>()
        for (rotation in 0..3) {
            for (flipped in 0..1) {
                val puzzlePoints = puzzleAsTile.rotatedPoints(rotation*90, flipped ==1).toSet()

                println("Trying to find seamonsters for ${rotation*90} and flipped: ${flipped ==1}")
                printPuzzle(puzzlePoints, withGaps = false)
                val hits = mutableSetOf<Point2D>()
                var hitCount = 0

                for (offsetX in min.x.roundToInt()..max.x.roundToInt()) {
                    for (offsetY in min.y.roundToInt()..max.y.roundToInt()) {
                        val seaMonsterPointsOffset = seaMonsterPoints.map { it + Point2D(offsetX, offsetY) }.toSet()

                        if (seaMonsterPointsOffset intersect puzzlePoints == seaMonsterPointsOffset) {
                            hits.addAll(seaMonsterPointsOffset)
                            hitCount++
                        }
                    }
                }

                allHits.addAll(hits)
                allHitCount.add(hitCount)
            }
        }

        println("Found hits: $allHits")
        println("Hitcount: $allHitCount")
        val roughSeas = puzzleWithoutBorders.size - allHits.size

        println("Found rough seas: $roughSeas")

        return productOfCornerPiecesIds to roughSeas
    }

    private fun convertToPoints(seaMonster: List<String>): List<Point2D> {
        printMatrixOfStrings(seaMonster)
        val seaMonsterEncoding = mutableListOf<Point2D>()
        for (row in seaMonster.indices) {
            for (col in seaMonster[row].indices) {
                if (seaMonster[row][col] == '#') {
                    seaMonsterEncoding.add(Point2D(col * 1.0, seaMonster.size - 1 - row * 1.0))
                }

            }
        }

        return seaMonsterEncoding
    }

    private fun placeACornerPiece(
        puzzle: MutableSet<Point2D>,
        cornerPieces: List<Tile>
    ): Tile {
        // start at current orientation.
        val startingTileLeftTop = cornerPieces[0]
        println("Tile ${startingTileLeftTop.id}:")
        printPuzzle(startingTileLeftTop.points)
        println("---")
        var rotation = 0;
        var sidesNeeded = listOf(Side.TOP, Side.RIGHT)
        while (true) {
            if (sidesNeeded.all { startingTileLeftTop.matches.contains(it) }) {
                val rotatedPoints: List<Point2D> = startingTileLeftTop.rotatedPoints(rotation)
                puzzle.addAll(rotatedPoints)
                startingTileLeftTop.processed = true
                startingTileLeftTop.offset = Point2D(0.0, 0.0)
                startingTileLeftTop.orientation = Orientation(rotation, false)
                println("Placed first piece! #${startingTileLeftTop.id}, ${startingTileLeftTop.orientation}, at ${startingTileLeftTop.offset}")
                break;
            }
            rotation += 90
            sidesNeeded = sidesNeeded.map { it.rotateCounterClockWise() }
        }
        return startingTileLeftTop
    }

    private fun applyMatches(puzzle: MutableSet<Point2D>, tile: Tile) {
        if (!tile.processed) {
            println("Tile #${tile.id} is already processed. Move along!")
            return
        }

        println("== Processing matches for tile ${tile.id}, located at ${tile.offset} == ")

        // Find orientation of tile
        println("Orientation of tile ${tile.orientation}")

        val unprocessedMatches = tile.matches.filter { !it.value.second.processed }

        for ((side, match) in unprocessedMatches) {
            if (match.second.processed) continue;

            val (matchingSide, matchingTile) = match

            println("Tile #${matchingTile.id}")
//            printPuzzle(matchingTile.points)
            // find orientation of side to match with
            val orientation: Side = side.rotate(tile.orientation.rotation).let {
                if (tile.orientation.flippedHorizontally && setOf(
                        Side.LEFT,
                        Side.FLEFT,
                        Side.RIGHT,
                        Side.FRIGHT
                    ).contains(it)
                ) it.horizontalFlip() else it
            }

            val offset: Point2D = tile.offset + orientation.unitVector * 10

            println("Side $side (${tile.id}) matches with $matchingSide (*${matchingTile.id}) ")
            println("Side $side (${tile.id}) is oriented at $orientation")
            println("Offset for tile ${matchingTile.id} on side $side (${tile.id}) is $offset")


            // the tile's matching side is located at orientation X
            // goal for matching tile is to
            // a) find opposing side of X, (oX) (which is the horizontal flip of X (e.g. left side of tile should match right side of other tile)
            // b) ensure orientation of matchingSide reaches oX (trial and error?)


            // a) opposing side
            val opposingSideToMatch = orientation.opposed()

            println("Opposing side too find: $opposingSideToMatch")


            // b) rotate until orientation found
            //      step 1) rotate until side reaches opposed side (or it flipped version)
            //      step 2) check if sides match.
            //              if so, okay
            //              otherwise, flip (vertical  if L, R, horizontal if T,B)


            var foundOrientation: Orientation = Orientation(0, false)
            var triedOrientation: Side = matchingSide
            while (triedOrientation != opposingSideToMatch && triedOrientation.flipped() != opposingSideToMatch) {
                triedOrientation = triedOrientation.rotate(90)
                foundOrientation = foundOrientation.copy(rotation = foundOrientation.rotation + 90)
            }

            println("Found orientation: $foundOrientation")
            println("Figuring out of a flip is needed")
            val rotatedPoints: List<Point2D> = matchingTile.rotatedPoints(foundOrientation.rotation).map { it }

            // get rotated points
            // filter edge
            val matchingTileSide = getSide(triedOrientation, rotatedPoints)

            // Get tile points
            val tilePoints = puzzle.map { it - tile.offset }
            val tileSide = getSide(orientation, tilePoints)

            if (areOpposingSides(tileSide, matchingTileSide)) {
                // yeah
                println("No flip is needed (orientation: $triedOrientation , $foundOrientation) ")
            } else {
                println("A flip is needed)")
                //flip horizontally or vertically
                when (triedOrientation) {
                    Side.LEFT, Side.FLEFT, Side.RIGHT, Side.FRIGHT -> {
                        // vertically
                        foundOrientation = foundOrientation.copy(
                            rotation = foundOrientation.rotation + 180,
                            flippedHorizontally = !foundOrientation.flippedHorizontally
                        )
                        triedOrientation = triedOrientation.rotate(180).horizontalFlip()

                    }
                    Side.TOP, Side.FTOP, Side.BOTTOM, Side.FBOTTOM -> {
                        // horizontally
                        foundOrientation =
                            foundOrientation.copy(flippedHorizontally = !foundOrientation.flippedHorizontally)
                        triedOrientation = triedOrientation.horizontalFlip()
                    }
                }
            }

            // triedOrientation is correct
            println("Found orientation: $foundOrientation (matching side went from $matchingSide to $triedOrientation)")

            // Then stitch puzzle,
            // update properties and
            // and call applyMatches
            matchingTile.offset = offset
            matchingTile.orientation = foundOrientation
            matchingTile.processed = true
            // Add to puzzle
            val points: List<Point2D> =
                matchingTile.rotatedPoints(foundOrientation.rotation, foundOrientation.flippedHorizontally)
                    .map { it + offset }
            puzzle.addAll(points)

            printPuzzle(puzzle)

            // and call applyMatches
            applyMatches(puzzle, matchingTile)
        }
    }

    private fun areOpposingSides(tileSide1: List<Point2D>, otherTileSide1: List<Point2D>): Boolean {
        // direction same

        val tileSide = tileSide1.map { it.toPoint2DInt() }.toList()
        val otherTileSide = otherTileSide1.map { it.toPoint2DInt() }
        println("Checking if opposing sides: $tileSide and $otherTileSide")
        printPuzzleInt(tileSide, Point2DInt(0, 0) to Point2DInt(9, 9))
        printPuzzleInt(otherTileSide, Point2DInt(0, 0) to Point2DInt(9, 9))
        val tileXs = tileSide.map { it.x }.toSet()
        val tileYs = tileSide.map { it.y }.toSet()

        val otherTileXs = otherTileSide.map { it.x }.toSet()
        val otherTileYs = otherTileSide.map { it.y }.toSet()

        val xIntersection = tileXs intersect otherTileXs
        val yIntersection = tileYs intersect otherTileYs

        val areOpposingSides = when {
            tileXs.size == 1 && xIntersection.isEmpty() -> yIntersection == tileYs
            tileYs.size == 1 && yIntersection.isEmpty() -> xIntersection == tileXs
            else -> false
        }
        println("sides are opposing? $areOpposingSides")
        return areOpposingSides
    }

    private fun getSide(side: Side, tilePoints: List<Point2D>): List<Point2D> {
        val xRange = when (side) {
            Side.LEFT, Side.FLEFT -> 0..0
            Side.RIGHT, Side.FRIGHT -> 9..9
            else -> 0..9
        }
        val yRange = when (side) {
            Side.BOTTOM, Side.FBOTTOM -> 0..0
            Side.TOP, Side.FTOP -> 9..9
            else -> 0..9
        }

        return tilePoints.filter { xRange.contains(it.x.roundToInt()) && yRange.contains(it.y.roundToInt()) }

    }

    private fun determinePossibleNeighboursForTiles(tiles: List<Tile>) {
        for (i in tiles.indices) {
            val tile = tiles[i]

            for (j in i + 1 until tiles.size) {
                val checkTile = tiles[j]
                if (tile == checkTile) continue

                for (entry in tile.sideEncodings) {
                    val checkTileSide = checkTile.sideEncodings[entry.key]
                    if (checkTileSide != null) {
                        // Checktile contains the same encoding!

                        val side = entry.value
                        checkForMatchCollisions(tile, side, checkTile, checkTileSide)
                        checkForMatchCollisions(checkTile, checkTileSide, tile, side)

                        tile.matches[side] = checkTileSide to checkTile
                        checkTile.matches[checkTileSide] = side to tile
                    }
                }
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


    private fun printPuzzleInt(roundedPuzzle: Iterable<Point2DInt>, bounds: Pair<Point2DInt, Point2DInt>? = null, withGaps: Boolean = true) {
        val min: Point2DInt
        val max: Point2DInt
        if (bounds == null) {
            min = roundedPuzzle.reduce { acc, p -> Point2DInt(minOf(acc.x, p.x), minOf(acc.y, p.y)) }
            max = roundedPuzzle.reduce { acc, p -> Point2DInt(maxOf(acc.x, p.x), maxOf(acc.y, p.y)) }
        } else {
            min = bounds.first
            max = bounds.second
        }


        val rowRange = max.y downTo min.y
        val colRange = min.x..max.x

        print(" ".repeat(5))
        colRange.forEach {
            val tens = abs((it / 10) % 10)
            val modulo = abs(it % 10)
            if (withGaps && modulo == 0 ) print(" ")
            print("${if (tens > 0) tens else " "}")
        }
        println()
        print(" ".repeat(5))
        colRange.forEach {
            val modulo = abs(it % 10)
            if (withGaps && modulo == 0) print(" ")
            print(modulo)
        }
        println()


        for (y in rowRange) {
            val y1 = "$y"
            print("$y${" ".repeat(5 - y1.length)}")
            for (x in colRange) {
                if (withGaps && x % 10 == 0) print(" ")
                if (roundedPuzzle.contains(Point2DInt(x, y))) print("X") else print(".")
            }
            println()
            if (withGaps && y % 10 == 0) println()
        }

    }

    private fun printPuzzle(puzzle: Iterable<Point2D>, bounds: Pair<Point2DInt, Point2DInt>? = null, withGaps: Boolean  = true) {
        val roundedPuzzle = puzzle.map { it.toPoint2DInt() }
        printPuzzleInt(roundedPuzzle, bounds, withGaps)

    }

    private fun checkForMatchCollisions(
        tile: Tile,
        side: Side,
        checkTile: Tile,
        checkTileSide: Side
    ) {
        val matchingTile = tile.matches[side]
        if (matchingTile != null && matchingTile.second != checkTile) {
            printAlreadyMatchingTile(tile, side, checkTile, checkTileSide, matchingTile)
        }

        val flippedSide = side.flipped()
        val matchingTile2 = tile.matches[flippedSide]
        if (matchingTile2 != null && matchingTile2.second != checkTile) {
            printAlreadyMatchingTile(tile, flippedSide, checkTile, checkTileSide, matchingTile2)
        }
    }

    private fun printAlreadyMatchingTile(
        tile: Tile,
        side: Side,
        otherTile: Tile,
        otherTilesSide: Side?,
        alreadyMatchingTile: Pair<Side, Tile>
    ) {
        val map = alreadyMatchingTile.second.matches
            .filter { it.key == alreadyMatchingTile.first }
            .map { "${it.key}=${it.value.second.id}" }

        println(
            "WARN MATCH ALREADY PRESENT: Tile ${tile.id}, $side, \twith ${otherTile.id}, $otherTilesSide" +
                    "\t(alreadyMatching tile: ${alreadyMatchingTile.second.id}, $map)"
        )
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
            val center = Point2D((subList[1].length - 1) / 2.0, (subList.size - 1) / 2.0)
            val points = mutableListOf<Point2D>()
            for (row in subList.indices) {
                for (col in subList[row].indices) {
                    if (subList[row][col] == '#') {
                        points.add(Point2D(col * 1.0, (subList.size - 1 - row) * 1.0))
                    }
                }
            }

//            printPuzzle(points)
//            printMatrixOfStrings(subList)

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

            val transposedX = transposeMatrix(subList.map { it.map { c -> c } }, '.', false)
            val transposed = transposedX.map { it.fold("") { acc, c -> acc + c } }
            for ((row, side) in listOf(0, transposed.size - 1).zip(listOf(Side.LEFT, Side.RIGHT))) {
                uniqueSides.add(transposed[row].fold("") { acc, i -> acc + i })

                val snd = transposed[row].toBinary()
                val fst = transposed[row].reversed().toBinary()
                allSideEncodings.add(fst)
                allSideEncodings.add(snd)
                sideEncodings[fst] = side
                sideEncodings[snd] = side.flipped()
            }


            Tile(id, points, uniqueSides, sideEncodings, center)
        }


    private fun String.toBinary(char: Char = '#'): Long =
        map { if (it == char) "1" else "0" }.fold("") { acc, i -> acc + i }.toLong(2)

}


fun main() {
    Day20.solve()
}
