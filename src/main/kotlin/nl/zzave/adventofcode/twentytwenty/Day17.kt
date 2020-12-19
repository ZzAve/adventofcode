package nl.zzave.adventofcode.twentytwenty

import day11.Day11
import nl.zzave.adventofcode.printMatrix

object Day17 {
    private val startingPoint = getTwentyTwentyFile("day17.data")
    fun solve(startingPoint: List<String> = Day17.startingPoint): Pair<Int, Int> {
        val initialEngineState: List<List<List<Char>>> =
            listOf(startingPoint.map { it.toCharArray().toList() })

        val gameOfEngine = playGameOfEngine(initialEngineState, 6)


        val nrOfActiveCubes = getActiveCubes(gameOfEngine)
        println("After 6 rounds, $nrOfActiveCubes are active")

        val gameOfHyperEngine = playGameOfHyperEngine(listOf(initialEngineState), 6)

        val nrOfActiveHyperCubes =
            gameOfHyperEngine.fold(0) { totalSum, w: List<List<List<Char>>> ->
                totalSum + getActiveCubes(w)
            }
        println("After 6 rounds of hyperengine, $nrOfActiveHyperCubes are active")

        return nrOfActiveCubes to nrOfActiveHyperCubes
    }

    private fun getActiveCubes(gameOfEngine: List<List<List<Char>>>) =
        gameOfEngine.fold(0) { totalSum: Int, slice: List<List<Char>> ->
            totalSum + slice.fold(0) { sliceSum, row ->
                sliceSum + row.count { it == '#' }
            }
        }

    private tailrec fun playGameOfHyperEngine(
        hyperEngineState: List<List<List<List<Char>>>>,
        iterations: Int
    ): List<List<List<List<Char>>>> {
        println("Iterations left: $iterations")

        val newHyperEngineState = mutableListOf<List<List<List<Char>>>>()
        var newW: MutableList<List<List<Char>>>
        var newSlice: MutableList<List<Char>>
        var newRow: MutableList<Char>
        for (w in -1..hyperEngineState.size) {
            newW = mutableListOf()
            for (slice in -1..hyperEngineState[0].size) {
                newSlice = mutableListOf()
                for (row in -1..hyperEngineState[0][0].size) {
                    newRow = mutableListOf()
                    for (column in -1..hyperEngineState[0][0][0].size) {
                        val activeCount =
                            getAdjacentCharacterCountLvl4(hyperEngineState, w, slice, row, column)
                        val char = when {
                            w in hyperEngineState.indices &&
                                    slice in hyperEngineState[0].indices &&
                                    row in hyperEngineState[0][0].indices &&
                                    column in hyperEngineState[0][0][0].indices &&
                                    hyperEngineState[w][slice][row][column] == '#' -> {
                                // was active
                                if (activeCount in 2..3) '#' else '.'
                            }
                            else ->
                                // was inactive
                                if (activeCount == 3) '#' else '.'
                        }

                        newRow.add(char)
                    }
                    newSlice.add(newRow)

                }
                newW.add(newSlice)
            }
            newHyperEngineState.add(newW)
        }

        newHyperEngineState.forEachIndexed { wIndex, list ->
            println("w=$wIndex")
            list.forEachIndexed { zIndex, z ->
                println("z=$zIndex")
                printMatrix(z)
                println()
            }
            println()
        }

        return if (iterations == 1) newHyperEngineState
        else playGameOfHyperEngine(newHyperEngineState, iterations - 1)


    }

    private tailrec fun playGameOfEngine(
        engineState: List<List<List<Char>>>,
        iterations: Int,
        excludeMiddle: Boolean = true
    ): List<List<List<Char>>> {
        println("Iterations left: $iterations")
        val newEngineState = mutableListOf<List<List<Char>>>()
        var newSlice: MutableList<List<Char>>
        var newRow: MutableList<Char>
        for (slice in -1..engineState.size) {
            newSlice = mutableListOf()
            for (row in -1..engineState[0].size) {
                newRow = mutableListOf()
                for (column in -1..engineState[0][0].size) {
                    val activeCount =
                        getAdjacentCharacterCountLvl3(engineState, slice, row, column, excludeMiddle = excludeMiddle)
                    val char = when {
                        slice in engineState.indices &&
                                row in engineState[0].indices &&
                                column in engineState[0][0].indices &&
                                engineState[slice][row][column] == '#' -> {
                            // was active
                            if (activeCount in 2..3) '#' else '.'
                        }
                        else ->
                            // was inactive
                            if (activeCount == 3) '#' else '.'
                    }

                    newRow.add(char)
                }
                newSlice.add(newRow)

            }
            newEngineState.add(newSlice)
        }

        newEngineState.forEachIndexed { index, list ->
            println("z=$index")
            printMatrix(list)
            println()
        }

        return if (iterations == 1) newEngineState
        else playGameOfEngine(newEngineState, iterations - 1)
    }

    private fun getAdjacentCharacterCountLvl4(
        engineState: List<List<List<List<Char>>>>,
        mw: Int,
        mslice: Int,
        mrow: Int,
        mseat: Int,
        adjacencyLimit: Int = 1
    ): Int {
        var count = 0
        val wRange = maxOf(0, (mw - adjacencyLimit))..minOf(engineState.size - 1, mw + adjacencyLimit)
        for (w in wRange) {
            val x = getAdjacentCharacterCountLvl3(
                engineState[w],
                mslice,
                mrow,
                mseat,
                adjacencyLimit,
                excludeMiddle = w == mw
            )
            count += x

        }
        return count
    }


    private fun getAdjacentCharacterCountLvl3(
        engineState: List<List<List<Char>>>,
        mslice: Int,
        mrow: Int,
        mseat: Int,
        adjacencyLimit: Int = 1,
        excludeMiddle: Boolean = false
    ): Int {
        var count = 0
        val sliceRange = maxOf(0, (mslice - adjacencyLimit))..minOf(engineState.size - 1, mslice + adjacencyLimit)
        for (slice in sliceRange) {
            val x = Day11.getAdjacentCharacterCount(
                engineState[slice],
                mrow,
                mseat,
                adjacencyLimit,
                '#',
                excludeMrowSeat = excludeMiddle && slice == mslice
            )
            count += x

        }
        return count
    }

}

fun main() {
    Day17.solve()
}