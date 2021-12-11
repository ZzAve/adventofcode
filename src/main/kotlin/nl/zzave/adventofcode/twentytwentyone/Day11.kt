package nl.zzave.adventofcode.twentytwentyone


object Day11 : TwentyTwentyOneProblem<Long> {
    override var debugMode: Boolean = true

    override fun solvePart1(input: List<String>): Long {
        val field = input.parseToDomain()
        prettyPrint(field, "-")
        val fieldAfterSimulation = simulate(100, field)

        return fieldAfterSimulation.second
    }

    private fun simulate(days: Int, currentState: Map<Coord, Int>, flashes: Long = 0): Pair<Map<Coord, Int>, Long> {
        if (days == 0) return currentState to flashes

        var updatedFlashes = flashes
        val updatedState = currentState.mapValues { it.value + 1 }
            .let { startState -> flashRecursively(startState) }
            .also { updatedFlashes += it.count { e -> e.value == 0 } }

        debug("With ${days - 1} days to go (flashes: $updatedFlashes)")
        debug(prettyPrint(updatedState))

        return simulate(days - 1, updatedState, updatedFlashes)
    }


    override fun solvePart2(input: List<String>): Long {
        val field = input.parseToDomain()
        log("Starting")
        prettyPrint(field)
        log("------")

        val fieldAfterSimulation = simulateUntilFlash(0, field)

        return fieldAfterSimulation.second
    }

    private fun simulateUntilFlash(days: Long, currentState: Map<Coord, Int>): Pair<Map<Coord, Int>, Long> {
        val updatedState = currentState.mapValues { it.value + 1 }
            .let { startState -> flashRecursively(startState) }
            .let {
                // Do we need to break?
                if (it.size == it.count { e -> e.value == 0 }) {
                    log("After ${days + 1} days")
                    prettyPrint(it, padStart = 3)
                    return it to days + 1
                }
                it
            }

        log("After ${days + 1} days")
        prettyPrint(updatedState, padStart = 3)

        return simulateUntilFlash(days + 1, updatedState)
    }

    private fun Map<Coord, OctopusState>.toState(): Map<Coord, Int> =
        mapValues { e ->
            if (e.value.hasFlashed) {
                0
            } else {
                e.value.energyLevel
            }
        }


    private fun flashRecursively(startState: Map<Coord, Int>): Map<Coord, Int> {
        var state = startState.mapValues { e -> OctopusState(e.value, false) }
        while (state.any { it.value.energyLevel > 9 && !it.value.hasFlashed }) {
            val newState = mutableMapOf<Coord, OctopusState>()
            state.forEach { (key, value) ->
                if (value.energyLevel > 9 && !value.hasFlashed) {
                    //up 9 places around it
                    for (x in maxOf(key.x - 1, 0)..minOf(key.x + 1, 9)) {
                        for (y in maxOf(key.y - 1, 0)..minOf(key.y + 1, 9)) {
                            val coord = Coord(x, y)
                            if (coord == key) {
                                newState[key] = value.copy(hasFlashed = true)
                            } else {

                                newState[coord] = (newState[coord] ?: state[coord]!!).addEnergy(1)
                            }
                        }
                    }
                } else newState[key] = newState[key] ?: value
            }

            state = newState
        }
        return state.toState()
    }


    private fun List<String>.parseToDomain(): Map<Coord, Int> =
        map { it.trim().toCharArray().map { c -> c.digitToInt() } }
            .flatMapIndexed { row: Int, rowEntries: List<Int> ->
                rowEntries.mapIndexed { col, colEntry ->
                    Coord(row, col) to colEntry
                }
            }
            .toMap()

    data class OctopusState(
        val energyLevel: Int,
        val hasFlashed: Boolean
    ) {
        fun addEnergy(i: Int): OctopusState {
            return OctopusState(energyLevel + i, hasFlashed)
        }
    }

}

fun main() {
    Day11.testSolution("day11-test.data", 1656, 195)
    println("--------- NOW FOR REALS --------")
    Day11.runSolution("day11.data")

}
