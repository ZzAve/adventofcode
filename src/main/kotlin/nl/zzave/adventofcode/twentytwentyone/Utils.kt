package nl.zzave.adventofcode.twentytwentyone

import nl.zzave.adventofcode.Problem
import nl.zzave.adventofcode.getFile
import kotlin.math.absoluteValue

fun getTwentyTwentyOneFile(filename: String) = getFile("twentytwentyone/$filename")

interface TwentyTwentyOneProblem<T> : Problem<T> {
    override fun getFile(filename: String): List<String> {
        return getTwentyTwentyOneFile(filename)
    }
}


data class Coord(
    val x: Int,
    val y: Int
) {

    private fun direction(): Pair<Double, Double> =
        this.x.absoluteValue * 1.0 / this.x to
                this.y.absoluteValue * 1.0 / this.y


    private operator fun minus(that: Coord): Coord = Coord(this.x - that.x, this.y - that.y)
}


fun <T> prettyPrint(map: Map<Coord, T>, default: String =  " ", padding: Char = ' ', padStart:Int = 1) {
    for (x in map.keys.minOf { it.x }..map.keys.maxOf { it.x }) {
        for (y in map.keys.minOf { it.y }..map.keys.maxOf { it.y }) {
            print("${map[Coord(x,y)] ?: default}".padStart(padStart, padding))
        }
        println("")
    }
    println("")
}
