package nl.zzave.adventofcode.twentytwenty

import getFile
import kotlin.math.absoluteValue


typealias Position = Pair<Long, Long>
typealias LocationVector = Pair<Position, Day12.Orientation>

object Day12 {
    private val instructions = getFile("twentytwenty/day12.data")

    enum class Orientation(val horizontal: Int, val vertical: Int, val direction: Int) {
        NORTH(0, 1, 0),
        EAST(1, 0, 90),
        SOUTH(0, -1, 180),
        WEST(-1, 0, 270);

        companion object {
            fun fromDirection(direction: Int): Orientation =
                values().first { it.direction == (direction + 360) % 360 }
        }
    }

    fun solve() {
        println("-- Using assumed instructions ---")
        val (assumedPosition, assumedOrientation) = followAssumedInstructions()
        println("Final position: $assumedPosition, orientation $assumedOrientation")
        println("Manhattan distance from start ${assumedPosition.first.absoluteValue + assumedPosition.second.absoluteValue}")


        println("-- Using actual instructions ---")
        val (position, orientation) = followActualInstructions()
        println("Final position: $position, orientation $orientation")
        println("Manhattan distance from start ${position.first.absoluteValue + position.second.absoluteValue}")
    }


    private fun followAssumedInstructions(): LocationVector {
        var position = 0L to 0L // start position
        var orientation = Orientation.EAST
        instructions.forEach {
            val action = it[0]
            val value = it.substring(1).toInt()
            when (action) {
                'N' -> position += move(Orientation.NORTH, value)
                'E' -> position += move(Orientation.EAST, value)
                'S' -> position += move(Orientation.SOUTH, value)
                'W' -> position += move(Orientation.WEST, value)
                'L', 'R' -> orientation = orientation.rotate(action, value)
                'F' -> position += move(orientation, value)
                else -> IllegalArgumentException("Ow noes, I don't understand action $action")
            }
        }
        return LocationVector(position, orientation)
    }

    private fun followActualInstructions(): LocationVector {
        var shipPosition = 0L to 0L // start position
        var wayPointPosition = 10L to 1L
        var orientation = Orientation.EAST
        instructions.forEach {
            val action = it[0]
            val value = it.substring(1).toInt()
            when (action) {
                'N' -> wayPointPosition += move(Orientation.NORTH, value)
                'E' -> wayPointPosition += move(Orientation.EAST, value)
                'S' -> wayPointPosition += move(Orientation.SOUTH, value)
                'W' -> wayPointPosition += move(Orientation.WEST, value)
                'L', 'R' -> {
                    orientation = orientation.rotate(action, value)
                    wayPointPosition = wayPointPosition.rotate(action, value)
                }
                'F' -> shipPosition += wayPointPosition * value
                else -> IllegalArgumentException("Ow noes, I don't understand action $action")
            }
        }

        return LocationVector(shipPosition, orientation)
    }

    private fun move(orientation: Orientation, value: Int): Position =
        (orientation.horizontal * value.toLong()) to (orientation.vertical * value.toLong())

    private fun Orientation.rotate(rotateAction: Char, value: Int): Orientation {
        val direction: Int = rotationDirection(rotateAction, value)

        return Orientation.fromDirection(this.direction + direction)
    }

    private fun Position.rotate(rotateAction: Char, value: Int): Position {
        val direction: Int = rotationDirection(rotateAction, value)

        return when ((direction + 360) % 360) {
            0 -> this
            90 -> Position(this.second, -this.first)
            180 -> Position(-this.first, -this.second)
            270 -> Position(-this.second, this.first)
            else -> throw IllegalArgumentException("I'm not fancy enough to deal with numbers not a multiple of 90")
        }
    }

    private fun rotationDirection(rotateAction: Char, value: Int): Int {
        return when (rotateAction) {
            'L' -> -value
            'R' -> value
            else -> throw IllegalArgumentException("I don't understand rotateAction $rotateAction")
        }
    }

    private operator fun Position.plus(move: Position): Position =
        (this.first + move.first) to (this.second + move.second)

    private operator fun Position.times(value: Int): Position =
        (this.first * value) to (this.second * value)
}


fun main() {
    Day12.solve()
}
