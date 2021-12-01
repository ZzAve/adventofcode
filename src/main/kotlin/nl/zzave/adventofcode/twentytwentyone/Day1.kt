package nl.zzave.adventofcode.twentytwentyone

import java.lang.Integer.parseInt


object Day1 {


    fun solvePart1(input: List<String>): Int {
        val depthMeasurements: List<Int> = input.map { parseInt(it) }

        return depthMeasurements.zipWithNext { a, b ->
            println("a $a vs b $b (= ${a < b})")
            if (a < b) 1 else 0
        }.sum()

    }

    fun solvePart2(input: List<String>): Int {
        val depthMeasurements: List<Int> = input.map { parseInt(it) }
        println(depthMeasurements)

        // TODO: y u so ugly?
        val depths = depthMeasurements.fold(Depths(mutableListOf(), mutableListOf())) { all, cur ->
            all.ongoing.add(cur)
            if (all.ongoing.size == 3) {
                all.depths.add(all.ongoing.sum())
                all.ongoing.removeAt(0)
            }
            all
        }


        println()
        println(depths.depths)

        return solvePart1(depths.depths.map {"$it" })

    }

    data class Depths(
        val depths: MutableList<Int>,
        val ongoing: MutableList<Int>
    )
}

fun main() {
    val input: List<String> = getTwentyTwentyOneFile("day1.data")
    val solvePart1 = Day1.solvePart1(input)
    println(solvePart1)

    val solvePart2 = Day1.solvePart2(input)
    println(solvePart2)
}
