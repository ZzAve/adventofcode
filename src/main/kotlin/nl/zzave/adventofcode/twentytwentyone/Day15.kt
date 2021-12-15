package nl.zzave.adventofcode.twentytwentyone

object Day15 : TwentyTwentyOneProblem<Long> {
    override var debugMode: Boolean = true

    override fun solvePart1(input: List<String>): Long {
        val cave = parseInput(input)

        prettyPrint(cave)

        cache.clear()

        // find a route (all down, all right)
        val bottomRowIndex = cave.maxOf { it.key.y }
        val referenceScore = cave.entries.mapNotNull { (key, value) ->
            when {
                key.x == 0 && key.y != 0 -> key to value.also { debugln("Taking $key") }
                key.y == bottomRowIndex -> key to value.also { debugln("Taking $key") }
                else -> null
            }
        }.let {
            it.map { a -> a.first } to it.sumOf { a -> a.second.toLong() }
        }
        logln("Reference score going down, then right: $referenceScore")

        // try all routes (depth first)
        val (path, risk) = tryAllRoutes(cave, referenceScore)

        logln("Found lowest risk: $risk for route: $path")
        return risk * 1L
    }

    val cache = mutableMapOf<Coord, Pair<List<Coord>, Long>>()
    private fun tryAllRoutes(
        cave: Map<Coord, Int>,
        referenceScore: Pair<List<Coord>, Long>,
        currentPath: List<Pair<Coord, Long>> = emptyList()
    ): Pair<List<Coord>, Long> {
        var bestScore = referenceScore
        val bottomRowIndex = cave.maxOf { it.key.y }
        val lastColIndex = cave.maxOf { it.key.x }
        val position = if (currentPath.isEmpty()) Coord(0, 0) else currentPath.last().first
        debugln("Arrived at $position from (${currentPath.pathOnly()})")

        val cacheEntry: Pair<List<Coord>, Long>? = cache[position]
        if (cacheEntry != null){
            val pathUpToPosition: List<Pair<Coord, Long>> = currentPath.takeWhile { it.first != position }

            val score = pathUpToPosition.sumOf { it.second } + cacheEntry.second

            val path = pathUpToPosition.map { it.first } + cacheEntry.first
            return path to score
        }

        // Early exit
        if (position.x == bottomRowIndex && position.y == lastColIndex) {
            val pathScore = currentPath.map { it.first } to currentPath.sumOf { it.second }
            val isBetter = pathScore.second < bestScore.second
            debugln("Reached bottom right. Pathscore: $pathScore. Current best $bestScore ${if (isBetter) "(will be updated)" else ""} ")
            return if (isBetter) pathScore else bestScore
        }


        val possibleOptions = listOfNotNull(
            if (position.x == lastColIndex) null else position.copy(x = position.x + 1),    //right
            if (position.y == bottomRowIndex) null else position.copy(y = position.y + 1),   //down
            if (position.x == 0) null else position.copy(x = position.x - 1),               //left
            if (position.y == 0) null else position.copy(y = position.y - 1),               //up
        )
            .filter { coord ->
                !(currentPath.map { it.first } + Coord(0, 0)).contains(coord)       // never travel somewhere twice
            }
            .filter { currentPath.sumOf { a -> a.second } + cave[it]!! < bestScore.second } // only consider a path if less risky

        possibleOptions.forEach {
            val score = tryAllRoutes(cave, bestScore, currentPath + (it to cave[it]!!.toLong()))


            if (score.second < bestScore.second){
                bestScore = score
            }
        }

        val pathForCache = bestScore.first.dropWhile { it != position }
        if(pathForCache.isNotEmpty()){
            cache[position] = pathForCache to pathForCache.sumOf { cave[it]!!.toLong() }
        }

//        cache[position] = bestScore
        return bestScore;
    }


    val positionToBestScoreCache = mutableMapOf<Coord, Pair<List<Coord>, Long>>()

    /**
     * Returns fastest route from a given position
     */
    private fun findFastestRouteRecursively(
        cave: Map<Coord, Int>,
        currentPosition: Coord = Coord(0, 0),
        bestScore: Pair<List<Coord>, Long>? = null
    ): Pair<List<Coord>, Long> {
        val bottomRowIndex = cave.maxOf { it.key.y }
        val lastColIndex = cave.maxOf { it.key.x }
        debug("Checking most risk averse  route from $currentPosition")

        val cacheResult = positionToBestScoreCache[currentPosition]
        if (cacheResult != null) {
            val path = cacheResult.first
            val risk = cacheResult.second
            debugln("\t[CACHE] Reached bottom right. Risk: $risk.\tPath is: $path)")

            return path to risk
        }

        // Early exit
        if (currentPosition.x == bottomRowIndex && currentPosition.y == lastColIndex) {
            val path = listOf(currentPosition)
            val risk = cave[currentPosition]?.toLong() ?: error("The cave just collapsed. Game over")

            debugln("\tReached bottom right.")

            positionToBestScoreCache[currentPosition] = path to risk
            return path to risk
        }


        val possibleOptions = listOfNotNull(
            if (currentPosition.x == lastColIndex) null else currentPosition.copy(x = currentPosition.x + 1),    //right
            if (currentPosition.y == bottomRowIndex) null else currentPosition.copy(y = currentPosition.y + 1),   //down
            if (currentPosition.x == 0) null else currentPosition.copy(x = currentPosition.x - 1),               //left
            if (currentPosition.y == 0) null else currentPosition.copy(y = currentPosition.y - 1),               //up
        )
            .also { debug("\tExploration options: $it") }

        var bestScore: Pair<List<Coord>, Long>? = null
        possibleOptions.forEach {
            val probableScore = bestScore?.let { b ->
                debug("\t there is a best score already. Therefore, the 'best' solution so far is to go directly back to here from that point.")
                listOf(currentPosition) + b.first to b.second + (cave[it] ?: error("Cave explosion"))
            }
            debugln("")
            val score = findFastestRouteRecursively(cave, it, probableScore)

            if (bestScore == null || score.second < (bestScore?.second ?: Long.MAX_VALUE)) {
                debugln("Route: ${score.first} is better than ${bestScore?.first}")
                bestScore = score
            }
        }

        positionToBestScoreCache[currentPosition] = bestScore!!

        return bestScore!!
    }


    private fun parseInput(input: List<String>): Map<Coord, Int> {
        return input.flatMapIndexed { row, rowValue ->
            rowValue.mapIndexed { col, value ->
                Coord(row, col) to value.digitToInt()
            }
        }.toMap()
    }

    override fun solvePart2(input: List<String>): Long {
        return -1L
    }
}

private fun List<Pair<Coord, Long>>.pathOnly(): String = joinToString { "(${it.first.x},${it.first.y})" }

fun main() {
    Day15.testSolution("day15-test.data", 40, -1)
    println("--------- NOW FOR REALS --------")
    Day15.runSolution("day15.data")

}

