package nl.zzave.adventofcode.twentytwentyone

object Day9 : TwentyTwentyOneProblem<Long> {
    override var debugMode: Boolean = false

    override fun solvePart1(input: List<String>): Long {
        val elevationMap = toElevationMap(input)


        return findLowPoints(elevationMap)
            .also { debug("Found low points $it") }
            .values.sumOf { it + 1L }
    }

    override fun solvePart2(input: List<String>): Long {
        val elevationMap = toElevationMap(input)
        val lowPoints = findLowPoints(elevationMap)

        // Assume all basin can run to 9 (and therefore not overlap)
        return lowPoints.map { (coord, _) ->
            log("")
            debug("----------------------")
            log("Exploring basin for low point $coord")
            debug("----------------------")
            addToBasinRecursively(coord, elevationMap)
                .also { log("Basin @ $coord: $it") }
        }.map { it.count() }
            .sortedDescending()
            .take(3)
            .fold(1L) { acc, i -> acc * i }
    }

    private fun addToBasinRecursively(
        coord: Coord,
        elevationMap: Map<Coord, Int>,
        basin: MutableMap<Coord, Int> = mutableMapOf()
    ): MutableMap<Coord, Int> {
        debug("Exploring basin expansion from $coord (current basin $basin)")

        val currentHeight = elevationMap[coord] ?: 10
        if (currentHeight > 8) return basin
        basin[coord] = currentHeight

        // coords to inspect
        val leftCoord = coord.copy(x = coord.x - 1)
        if (isPartOfBasin(leftCoord, currentHeight, elevationMap, basin)) {
            debug("Found $leftCoord to be part of the current basin")
            basin.putAll(addToBasinRecursively(leftCoord, elevationMap, basin))
        }

        val rightCoord = coord.copy(x = coord.x + 1)
        if (isPartOfBasin(rightCoord, currentHeight, elevationMap, basin)) {
            debug("Found $rightCoord to be part of the current basin")
            basin.putAll(addToBasinRecursively(rightCoord, elevationMap, basin))
        }

        val downCoord = coord.copy(y = coord.y - 1)
        if (isPartOfBasin(downCoord, currentHeight, elevationMap, basin)) {
            debug("Found $downCoord to be part of the current basin")
            basin.putAll(addToBasinRecursively(downCoord, elevationMap, basin))
        }

        val upCoord = coord.copy(y = coord.y + 1)
        if (isPartOfBasin(upCoord, currentHeight, elevationMap, basin)) {
            debug("Found $upCoord to be part of the current basin")
            basin.putAll(addToBasinRecursively(upCoord, elevationMap, basin))
        }

        return basin
    }

    private fun isPartOfBasin(
        adjacentCoord: Coord,
        referenceHeight: Int,
        elevationMap: Map<Coord, Int>,
        basin: Map<Coord, Int>
    ): Boolean {
        val height = elevationMap[adjacentCoord] ?: 10
        return !basin.contains(adjacentCoord) && height in (referenceHeight + 1)..8
    }

    private fun findLowPoints(elevationMap: Map<Coord, Int>) =
        elevationMap.filter { (c, height) ->
            height < (elevationMap[c.copy(x = c.x - 1)] ?: 10) &&
                    height < (elevationMap[c.copy(x = c.x + 1)] ?: 10) &&
                    height < (elevationMap[c.copy(y = c.y - 1)] ?: 10) &&
                    height < (elevationMap[c.copy(y = c.y + 1)] ?: 10)
        }

    private fun toElevationMap(input: List<String>): Map<Coord, Int> = input
        .flatMapIndexed { row, it ->
            debug("Mapping $it")
            it.trim().toCharArray()
                .mapIndexed { col, height -> Coord(col, row) to height.digitToInt() }
        }
        .toMap()


    data class Coord(
        val x: Int,
        val y: Int,
    )
}


fun main() {
    Day9.debugMode = true
    Day9.testSolution("day9-test.data", 15, 1134)

    println("--------- NOW FOR REALS --------")

    Day9.debugMode = false
    Day9.runSolution("day9.data")
}
