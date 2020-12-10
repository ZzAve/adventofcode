package day10

import getFile

object Day10 {
    private val chargerOutputRatings = getFile("day10.data").map { it.toInt() }

    private val maxElement = chargerOutputRatings.maxOrNull()!! + 3
    private val sorted =
        (chargerOutputRatings + listOf(0, maxElement)).sorted()

    fun solve() {
        val (oneJoltDiffs, twoJoltDiffs, threeJoltDiffs) = getJoltDiffsWHenAllAreChained(sorted)
        println("oneJoltDifferences $oneJoltDiffs")
        println("twoJoltDifferences $twoJoltDiffs")
        println("threeJoltDifferences $threeJoltDiffs")
        println("Product of 1-jolt and 3-jolt diffs : ${oneJoltDiffs * threeJoltDiffs}")

        val chunks = splitChunks(sorted)
        val combinations = chunks.map {
            getCombinations(it, 0)

        }.reduce { acc:Long, i -> acc * i }
        println("Nr of possible combinations: $combinations")


    }

    private fun splitChunks(chargers: List<Int>): MutableList<List<Int>> {
        val chunks = mutableListOf<List<Int>>()
        var currentChunk = mutableListOf<Int>(chargers[0])
        for (idx in 1 until chargers.size){
            if ( chargers[idx] - chargers[idx-1] == 3){
                chunks.add(currentChunk)
                currentChunk = mutableListOf(chargers[idx])
            } else {
                currentChunk.add(chargers[idx])

            }
        }
        chunks.add(currentChunk)
        return chunks
    }

    // Split array into chuncks that lie between 3 things apart (the first and last need to exist)
    // for each chunck determine combinations
    // take product of combinations of chunk

    private fun getCombinations(chunk: List<Int>, startIndex: Int): Long {
        if (chunk.size ==1 ) return 1
        if (startIndex >= chunk.size) return 0
        if (startIndex == chunk.size - 1) return 1

        val withOneStep =
            if (startIndex < chunk.size - 1 &&
                chunk[startIndex + 1] - chunk[startIndex] <= 3
            ) getCombinations(chunk, startIndex + 1)
            else 0

        val withTwoSteps =
            if (startIndex < chunk.size - 2 &&
                chunk[startIndex + 2] - chunk[startIndex] <= 3
            ) getCombinations(chunk, startIndex + 2)
            else 0

        val withThreeSteps =
            if (startIndex < chunk.size - 3 &&
                chunk[startIndex + 3] - chunk[startIndex] <= 3
            ) getCombinations(chunk, startIndex + 3)
            else 0

        return withOneStep + withTwoSteps + withThreeSteps

    }

    private fun getJoltDiffsWHenAllAreChained(sorted: List<Int>): Triple<Int, Int, Int> {
        var oneJoltDiffs = 0
        var twoJoltDiffs = 0
        var threeJoltDiffs = 0
        sorted
            .zipWithNext()
            .forEach { (prev, curr) ->
                when (curr - prev) {
                    3 -> threeJoltDiffs++
                    1 -> oneJoltDiffs++
                    2 -> twoJoltDiffs++
                    else -> throw RuntimeException("PANIC! Output rating gap is bigger than 3 (or <1)")
                }
            }

        return Triple(oneJoltDiffs, twoJoltDiffs, threeJoltDiffs)
    }
}

fun main() {
    try {

        Day10.solve()
    } catch (e: Exception) {
        println(e)
    }
}