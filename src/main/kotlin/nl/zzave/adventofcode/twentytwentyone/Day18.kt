package nl.zzave.adventofcode.twentytwentyone

import nl.zzave.adventofcode.twentytwentyone.Day18.Direction.END
import nl.zzave.adventofcode.twentytwentyone.Day18.Direction.LEFT
import nl.zzave.adventofcode.twentytwentyone.Day18.Direction.RIGHT
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

object Day18 : TwentyTwentyOneProblem<Long> {
    override var debugMode: Boolean = true

    override fun solvePart1(input: List<String>): Long {
        val snailFishNumbers = parseSnailFishNumbers(input)
        val reducedNumber = addSnailfishNumbers(snailFishNumbers)

        val magnitude = reducedNumber.magnitude()
        logln("Calculated magnitude:\t$magnitude")
        return magnitude
    }

    private fun addSnailfishNumbers(snailFishNumbers: List<SnailFishNestedNumber>): SnailFishNestedNumber {
        val reducedNumber = snailFishNumbers.reduce { reducedNumber, snailFishNestedNumber ->
            val newSnailfishNumber = reducedNumber + snailFishNestedNumber
            logln("after addition:\t$newSnailfishNumber")
            newSnailfishNumber.reduce()
            debugln("Magnitude afterc reduction: ${newSnailfishNumber.magnitude()}")
            logln("")
            newSnailfishNumber
        }
        return reducedNumber
    }

    private fun parseSnailFishNumbers(input: List<String>): List<SnailFishNestedNumber> = input.map {
        check(it.first() == '[') { "Snailfishnumber should start with [" }
        check(it.last() == ']') { "Snailfishnumber should end with ]" }
        parseSnailFishNumber(it).first
    }

    private fun parseSnailFishNumber(rawNumber: String, index: Int = 0): Pair<SnailFishNestedNumber, Int> {
        check(rawNumber[index] == '[') { "Snailfishnumber should start with [" }

        val endLeftIndex: Int
        //parseLeftSide
        val leftSide: SnailFishNumberElement
        if (rawNumber[index + 1] == '[') {
            val result = parseSnailFishNumber(rawNumber, index + 1)
            endLeftIndex = result.second
            leftSide = result.first
        } else {
            leftSide = SnailFishLiteral(rawNumber
                .drop(index + 1)
                .takeWhile { it != ',' }
                .also { endLeftIndex = index + it.length }
                .toInt()
            )
        }

        check(rawNumber[endLeftIndex + 1] == ',') { "Snailfishnumber format misses a ',' should be [<leftSide>,<rightSide>]" }
        val endRightIndex: Int
        //parseRightSide
        val rightSide: SnailFishNumberElement
        if (rawNumber[endLeftIndex + 2] == '[') {
            val result = parseSnailFishNumber(rawNumber, endLeftIndex + 2)
            endRightIndex = result.second
            rightSide = result.first
        } else {
            rightSide = SnailFishLiteral(rawNumber
                .drop(endLeftIndex + 2)
                .takeWhile { it != ']' }
                .also { endRightIndex = endLeftIndex + 1 + it.length }
                .toInt()
            )
        }


        check(rawNumber[endRightIndex + 1] == ']') { "Snailfishnumber should end with ]" }

        return SnailFishNestedNumber(leftSide, rightSide) to endRightIndex + 1

    }

    override fun solvePart2(input: List<String>): Long {
        val snailFishNumbers = parseSnailFishNumbers(input)

        var largestPossibleMagnitude = 0L
        var bestNumbersToAdd: Pair<SnailFishNumberElement, SnailFishNumberElement>? = null
        for (firstNumber in snailFishNumbers.indices) {
            for (secondNumber in snailFishNumbers.indices) {
                if (firstNumber == secondNumber) continue

                val numbersToAdd =
                    listOf(
                        snailFishNumbers[firstNumber].deepCopy() as SnailFishNestedNumber,
                        snailFishNumbers[secondNumber].deepCopy() as SnailFishNestedNumber
                    )
                val reducedNumber = addSnailfishNumbers(numbersToAdd)
                val magnitude = reducedNumber.magnitude()
                logln("Calculated magnitude:\t$magnitude")

                if (magnitude > largestPossibleMagnitude) {
                    logln("==============================")
                    logln("=== BIGGER MAGNITUDE FOUND ===")
                    logln("==== ${magnitude.toString().padStart(11, ' ').padEnd(22, ' ')} ====")
                    logln("==============================")
                    logln("Calculated magnitude:\t$magnitude")
                    largestPossibleMagnitude = magnitude
                    bestNumbersToAdd = snailFishNumbers[firstNumber] to snailFishNumbers[secondNumber]
                }
            }
        }

        logln("Largest possible magnitude: $largestPossibleMagnitude")
        logln("Best numbers: ${bestNumbersToAdd?.first} and ${bestNumbersToAdd?.second}")
        return largestPossibleMagnitude
    }


    sealed class SnailFishNumberElement(open val id: UUID) {

        operator fun plus(other: SnailFishNumberElement): SnailFishNestedNumber {
            return this.add(other)
        }

        fun add(other: SnailFishNumberElement): SnailFishNestedNumber {
            val snailFishNestedNumber = SnailFishNestedNumber(this, other)
            debugln("Adding:\t$this with\t$other")
            return snailFishNestedNumber
        }

        fun reduce() {
            var firstNestedPair: List<Pair<SnailFishNestedNumber, Direction>>?
            var firstLiteralGreaterThan9: Pair<SnailFishNestedNumber, Direction>? = null

            do {
                //try explode
                // find first 4-nested pair
                firstNestedPair = this.firstNestedPair(4)
                if (firstNestedPair != null) {
                    explodePair(firstNestedPair)
                    logln("after explode:\t$this")
                }

                // if not exploded, try split
                else {
                    firstLiteralGreaterThan9 = this.findFirstLiteralGreaterThan(9)
                    if (firstLiteralGreaterThan9 != null) {
                        splitLiteralElement(firstLiteralGreaterThan9.first, firstLiteralGreaterThan9.second)
                        logln("after split:\t$this")
                    }
                }
            } while (!(firstNestedPair == null && firstLiteralGreaterThan9 == null))


        }

        private fun splitLiteralElement(parentElement: SnailFishNestedNumber, childDirectionOfLiteral: Direction) {
            debugln("Splitting $parentElement, $childDirectionOfLiteral")
            val literal = parentElement
                .let { if (childDirectionOfLiteral == LEFT) it.left else it.right } as SnailFishLiteral

            val newElement = SnailFishNestedNumber(
                left = SnailFishLiteral(floor(literal.value / 2.0).roundToInt()),
                right = SnailFishLiteral(ceil(literal.value / 2.0).roundToInt())
            )

            when (childDirectionOfLiteral) {
                LEFT -> parentElement.left = newElement
                RIGHT -> parentElement.right = newElement
                else -> error("💣💥")
            }
        }

        private fun findFirstLiteralGreaterThan(i: Int): Pair<SnailFishNestedNumber, Direction>? {
            if (this is SnailFishLiteral) return null
            check(this is SnailFishNestedNumber) { "Should not occur" }

            return when {
                this.left is SnailFishLiteral && (this.left as SnailFishLiteral).value > i -> this to LEFT
                else -> {
                    val findFirstLiteralGreaterThanI = left.findFirstLiteralGreaterThan(i)
                    when {
                        findFirstLiteralGreaterThanI != null -> findFirstLiteralGreaterThanI
                        this.right is SnailFishLiteral && (this.right as SnailFishLiteral).value > i -> this to RIGHT
                        this.right is SnailFishLiteral -> null
                        else -> right.findFirstLiteralGreaterThan(i)
                    }
                }
            }

        }

        private fun explodePair(firstNestedPair: List<Pair<SnailFishNestedNumber, Direction>>) {
            // explode last pair
            val elementToExplode = firstNestedPair.last()
            debugln("Exploding $elementToExplode")

            val left = elementToExplode.first.left as SnailFishLiteral
            val right = elementToExplode.first.right as SnailFishLiteral

            // There are literals left of the element to explode only if the path to the element contains a 'right' turn
            firstNestedPair.lastOrNull { it.second == RIGHT }
                ?.first
                ?.left
                ?.updateRightMostLiteral(left, elementToExplode.first)


            // There are literals right of the element to explode only if the path to the element contains a 'right' turn
            firstNestedPair.lastOrNull { it.second == LEFT }
                ?.first
                ?.right
                ?.updateLeftMostLiteral(right, elementToExplode.first)

            // Update parent of the exploded node
            val (parentElement, direction) = firstNestedPair[firstNestedPair.size - 2]
            val newSnailfishElement = SnailFishLiteral(0)
            if (direction == LEFT) {
                check((parentElement.left as SnailFishNestedNumber).left is SnailFishLiteral) { "Expected element to be a literal" }
                parentElement.left = newSnailfishElement
            } else {
                check((parentElement.right as SnailFishNestedNumber).right is SnailFishLiteral) { "Expected element to be a literal" }
                parentElement.right = newSnailfishElement
            }
        }

        private fun updateRightMostLiteral(
            snailFishLiteral: SnailFishLiteral,
            elementToAvoid: SnailFishNumberElement
        ) {
            var element = this
            while (element is SnailFishNestedNumber) {
                element = if (element.right == elementToAvoid) element.left else element.right
            }

            (element as SnailFishLiteral).value += snailFishLiteral.value

        }

        private fun updateLeftMostLiteral(
            snailFishLiteral: SnailFishLiteral,
            elementToAvoid: SnailFishNumberElement
        ) {
            var element = this
            while (element is SnailFishNestedNumber) {
                element = if (element.left == elementToAvoid) element.right else element.left
            }

            (element as SnailFishLiteral).value += snailFishLiteral.value
        }

        private fun firstNestedPair(nestedLevel: Int): List<Pair<SnailFishNestedNumber, Direction>>? = when {
            nestedLevel == 0 && this is SnailFishNestedNumber -> listOf(this to END)
            this is SnailFishLiteral -> null
            this is SnailFishNestedNumber -> {
                val left = this.left.firstNestedPair(nestedLevel - 1)
                    ?.let { listOf(this to LEFT) + it }

                left ?: right.firstNestedPair(nestedLevel - 1)
                    ?.let { listOf(this to RIGHT) + it }

            }
            else -> null
        }

        fun magnitude(): Long = when (this) {
            is SnailFishLiteral -> this.value.toLong()
            is SnailFishNestedNumber -> 3 * this.left.magnitude() + 2 * this.right.magnitude()
        }

        fun deepCopy(): SnailFishNumberElement {
            return when (this) {
                is SnailFishLiteral -> SnailFishLiteral(this.value)
                is SnailFishNestedNumber -> SnailFishNestedNumber(this.left.deepCopy(), this.right.deepCopy())
            }
        }
    }

    enum class Direction {
        LEFT, RIGHT, END
    }

    data class SnailFishNestedNumber(
        override val id: UUID,
        var left: SnailFishNumberElement,
        var right: SnailFishNumberElement
    ) : SnailFishNumberElement(id) {

        constructor(left: SnailFishNumberElement, right: SnailFishNumberElement) : this(
            UUID.randomUUID(),
            left,
            right
        )


        override fun toString(): String {
            return "[$left,$right]"
        }
    }

    data class SnailFishLiteral(
        override val id: UUID,
        var value: Int
    ) : SnailFishNumberElement(id) {

        constructor(literalValue: Int) : this(UUID.randomUUID(), literalValue)

        override fun toString(): String {
            return "$value"
        }
    }
}

fun main() {
//    Day18.testSolution(listOf("[[[[4,3],4],4],[7,[[8,4],9]]]", "[1,1]"), 1384, -1)
//    Day18.testSolution(
//        listOf(
//            "[1,1]",
//            "[2,2]",
//            "[3,3]",
//            "[4,4]",
//            "[5,5]"
//        ), 791
//    )
//    Day18.testSolution(
//        listOf(
//            "[1,1]",
//            "[2,2]",
//            "[3,3]",
//            "[4,4]",
//            "[5,5]",
//            "[6,6]"
//        ), 1137
//    )
//    Day18.testSolution(listOf("[[[1,1],[2,2]],[3,3]]", "[4,4]"), 445)
//    Day18.testSolution(
//        listOf(
//            "[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]",
//            "[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]"
//        ), 4014
//    )
    Day18.testSolution(
        listOf(
            "[[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]",
            "[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]"
        ), 4105
    )
    Day18.testSolution(listOf("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]"), 3488)
    Day18.testSolution("day18-test.data", 4140, 3993)
    println("--------- NOW FOR REALS --------")
    Day18.runSolution("day18.data")
}
