package nl.zzave.adventofcode.twentytwentyone

object Day25 : TwentyTwentyOneProblem<Long> {
    override var debugMode: Boolean = true

    override fun solvePart1(input: List<String>): Long {
        val (locations, playingField) = parseInput(input)
        val (xMax, yMax) = playingField
        printField(locations)

        var currentLocations: Map<Coord, Direction>
        var newLocations = locations
        var steps = 0
        do {
            currentLocations = newLocations
            newLocations = step(currentLocations, xMax, yMax)
            steps++

            debugln("After $steps steps:")
            if (debugMode) printField(newLocations)
            debugln("")

        } while (currentLocations != newLocations)

        return steps.toLong()
    }

    override fun solvePart2(input: List<String>): Long {
        return -1L
    }


    private fun printField(locations: Map<Coord, Direction>) {
        prettyPrint(locations.mapValues {
            when (it.value) {
                Direction.SOUTH -> 'v'
                Direction.EAST -> '>'
            }
        }, default = ".")
    }

    private fun step(locations: Map<Coord, Direction>, xMax: Int, yMax: Int): Map<Coord, Direction> {
        //move all east-erlings
        val locationsAfterEastHerd: Map<Coord, Direction> = locations
            .map {
                when {
                    it.value != Direction.EAST -> it.key to it.value
                    it.value == Direction.EAST -> {
                        val neighbour = it.key.neighbour(it.value, xMax, yMax)
                        if (locations.contains(neighbour)) {
                            it.key to it.value
                        } else {
                            neighbour to it.value
                        }

                    }
                    else -> error("Assumption mistake?")
                }
            }
            .toMap()

        val locationsAfterSouthHerd: Map<Coord, Direction> = locationsAfterEastHerd
            .map {
                when {
                    it.value != Direction.SOUTH -> it.key to it.value
                    it.value == Direction.SOUTH -> {
                        val neighbour = it.key.neighbour(it.value, xMax, yMax)
                        if (locationsAfterEastHerd.contains(neighbour)) {
                            it.key to it.value
                        } else {
                            neighbour to it.value
                        }

                    }
                    else -> error("Assumption mistake?")
                }
            }
            .toMap()

        return locationsAfterSouthHerd

    }


    private fun parseInput(input: List<String>): Pair<Map<Coord, Direction>, Pair<Int, Int>> {
        val playingField = input[0].length to input.size
        val locations = input.flatMapIndexed { yIndex, row ->
            row.mapIndexedNotNull { xIndex, value ->
                val direction: Direction? = when (value) {
                    '>' -> Direction.EAST
                    'v' -> Direction.SOUTH
                    '.' -> null
                    else -> error("Something, other than a seacucumber is lurking on the ocean bottom")
                }

                direction?.let {
                    Coord(xIndex, yIndex) to it
                }
            }
        }.toMap()
        return locations to playingField
    }

    enum class Direction {
        EAST, SOUTH
    }

    private fun Coord.neighbour(direction: Direction, xMax: Int, yMax: Int): Coord {
        return when (direction) {
            Direction.EAST -> this.copy(x = (x + 1) % xMax)
            Direction.SOUTH -> this.copy(y = (y + 1) % yMax)
        }
    }
}

fun main() {
    Day25.testSolution("day25-test-2.data", 58, -1)
    println("--------- NOW FOR REALS --------")
    Day25.runSolution("day25.data")
}
