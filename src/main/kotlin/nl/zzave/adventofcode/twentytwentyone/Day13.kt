package nl.zzave.adventofcode.twentytwentyone

import nl.zzave.adventofcode.twentytwentyone.Day13.FoldInstruction.Axis.X
import nl.zzave.adventofcode.twentytwentyone.Day13.FoldInstruction.Axis.Y
import kotlin.math.absoluteValue

object Day13 : TwentyTwentyOneProblem<Long> {
    override var debugMode: Boolean = true

    override fun solvePart1(input: List<String>): Long {
        val (paper: Set<Coord>, instructions: List<FoldInstruction>) = parsePaper(input)

        prettyPrint(paper, "X", "-")
        val newPaper = foldLikeACrazyElf(instructions.take(1), paper)

        return 1L * newPaper.size

    }

    private fun foldLikeACrazyElf(
        instructions: List<FoldInstruction>,
        paper: Set<Coord>
    ): Set<Coord> {
        val newPaper = instructions.fold(paper) { currentPaper, instruction ->
            log("Applying $instruction")
            if (debugMode) prettyPrint(currentPaper, "X", "-")

            val shifted = currentPaper.map {
                when (instruction.axis) {
                    X -> it.copy(x = it.x - instruction.location)
                    Y -> it.copy(y = it.y - instruction.location)
                }
            }

            val negAbsolute = shifted.map {
                when (instruction.axis) {
                    X ->   it.copy(x = it.x.absoluteValue * -1)
                    Y -> it.copy( y = it.y.absoluteValue * -1)
                }

            }.toSet()

            val filteredFoldLine = negAbsolute.filter {
                when (instruction.axis) {
                    X -> it.x != 0
                    Y -> it.y != 0
                }
            }.toSet()

            val minx = filteredFoldLine.minOf { it.x }
            val miny = filteredFoldLine.minOf { it.y }

            val newPaper = filteredFoldLine.map {
                when (instruction.axis) {
                    X -> it.copy(x = it.x - minx)
                    Y -> it.copy(y = it.y - miny)
                }
            }

            prettyPrint(
                newPaper.map { Coord(it.x , it.y )}.toSet(), "X", " ", padStart = 1
            )
            println()
            newPaper.toSet()
        }
        return newPaper
    }

    private fun parsePaper(input: List<String>): Pair<Set<Coord>, List<FoldInstruction>> {
        val dots = input.takeWhile { it.isNotBlank() }
            .map {
                val (x, y) = it.trim().split(",")
                Coord(x.toInt(), y.toInt())
            }.toSet()

        ///fold along y=7
        val foldInstructions = input.takeLastWhile { it.isNotBlank() }
            .map {
                val (axis, value) = it.trim().split(" ").last().split("=")
                FoldInstruction(FoldInstruction.Axis.valueOf(axis.uppercase()), value.toInt())
            }


        return dots to foldInstructions
    }

    override fun solvePart2(input: List<String>): Long {
        val (paper: Set<Coord>, instructions: List<FoldInstruction>) = parsePaper(input)

//        prettyPrint(paper, "X", "-")
        val newPaper = foldLikeACrazyElf(instructions, paper)
        println(newPaper)

        val maxOfx = newPaper.maxOf { it.x }
        val maxOfy = newPaper.maxOf { it.y }
        log("Size: $maxOfx to $maxOfy")
//        for (letter in 0..maxOfx step maxOfx / 8) {
//            val letterCoords = newPaper.filter { it.x >= letter && it.x <= letter + maxOfx }
//            prettyPrint(
//                letterCoords.map { Coord(it.x / 15, it.y / 15) }.toSet(), "X", "-", padStart = 4
//            )

//        }
        prettyPrint(
                newPaper.map { Coord(it.x , it.y )}.toSet(), "X", " ", padStart = 1
            )

        return -1

    }

    data class FoldInstruction(
        val axis: Axis,
        val location: Int
    ) {

        enum class Axis {
            X, Y
        }
    }
}


fun main() {
//    Day13.testSolution("day13-test.data", 17, -1)
    println("--------- NOW FOR REALS --------")
    Day13.runSolution("day13.data")

}
