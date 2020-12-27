package nl.zzave.adventofcode.utils

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class Point2DInt(
    val x: Int,
    val y: Int
)


private const val EPSILON = 0.01
data class Point2D(
    val x: Double,
    val y: Double

) {
    constructor(x: Int, y: Int) : this(
        x*1.0,
        y*1.0
    )

    fun rotated(degrees: Int): Point2D {
        val rad = degrees / 180.0 * Math.PI
        return Point2D(
            x * cos(rad) - y * sin(rad),
            x * sin(rad) + y * cos(rad)
        )

    }

    fun flippedHorizontally(): Point2D {
        return Point2D(-x, y)
    }

    fun toPoint2DInt() = Point2DInt(x.roundToInt(), y.roundToInt())


    // To allow for rounding issues, implement hashcode and equals method
    override fun hashCode(): Int {
        return 100 * (this.x * 1 / EPSILON).roundToInt() + (this.y * 1 / EPSILON).roundToInt()
    }

    // To allow for rounding issues, implement hashcode and equals method
    override fun equals(other: Any?): Boolean =
        if (other !is Point2D) false
        else abs(this.x - other.x) < EPSILON && abs(this.y - other.y) < EPSILON

    operator fun plus(other: Point2D) = Point2D(x + other.x, y + other.y)
    operator fun times(i: Int) = Point2D(x * i, y * i)
    operator fun minus(other: Point2D) = Point2D(x - other.x, y - other.y)
}
