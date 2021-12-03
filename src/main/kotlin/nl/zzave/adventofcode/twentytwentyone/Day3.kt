package nl.zzave.adventofcode.twentytwentyone

import nl.zzave.adventofcode.Problem


object Day3 : Problem<Int> {


    override fun solvePart1(input: List<String>): Int {

        val bitOccurrences = input
            .map { line ->
                line.map { "$it".toInt() }
                    .also { println(it) }
            }
            .reduce { acc, cur ->
                acc.mapIndexed { index, i -> i + cur[index] }
                    .also {
                        println()
                        println(acc)
                        println(cur)
                        println("====")
                        println(it)

                    }
            }

        val gammaRate = bitOccurrences
            .map { if (it > input.size / 2.0) 1 else 0 }
            .joinToString("")
            .toInt(2)

        val epsilonRate = bitOccurrences
            .map { if (it < input.size / 2.0) 1 else 0 }
            .joinToString("")
            .toInt(2)

        return gammaRate * epsilonRate
    }


    override fun solvePart2(input: List<String>): Int {
        val numbers = input.map { it.toInt(2) }

        val oxygenMagicNumber = findMagicNumber(numbers.map { it to it }, Strategy.MOST_COMMON_BIT).second
        val co2MagicNumber = findMagicNumber(numbers.map { it to it }, Strategy.LEAST_COMMON_BIT).second

        return oxygenMagicNumber * co2MagicNumber
    }

    private fun findMagicNumber(numbers: List<Pair<Int, Int>>, strategy: Strategy): Pair<Int, Int> {
        println("Finding magic number ($strategy)")
        println(numbers)
        val highestSignificantBit = numbers.map { it.first }.maxOf { it.takeHighestOneBit() }
        val occurrencesCommonBit = numbers.map { it.first }.count { it >= highestSignificantBit }.also { println("Occurrences common bit: $it") }

        val filteredNumbers = when (strategy) {
            Strategy.LEAST_COMMON_BIT -> {
                if (occurrencesCommonBit < numbers.size / 2.0) {
                    // 1 is least common (or it's a tie)
                    numbers.filter { it.first.takeHighestOneBit() >= highestSignificantBit }
                        .map { it.first - highestSignificantBit to it.second }
                } else {
                    // 0 is least common
                    numbers.filter { it.first.takeHighestOneBit() < highestSignificantBit }
                }
            }
            Strategy.MOST_COMMON_BIT -> {
                if (occurrencesCommonBit >= numbers.size / 2.0) {
                    // 1 is most common (or it's a tie)
                    numbers.filter { it.first.takeHighestOneBit() >= highestSignificantBit }
                        .map { it.first - highestSignificantBit to it.second }
                } else {
                    // 0 is most common
                    numbers.filter { it.first.takeHighestOneBit() < highestSignificantBit }
                }
            }
        }

        println("Filtered numbers ${filteredNumbers.map { it.second.toString(2) }}")

        return if (filteredNumbers.size == 1) {
            println("Done")
            filteredNumbers[0]
        } else {
            filteredNumbers
                .also { println("Bit shifted and trying again");println() }
                .let { findMagicNumber(it, strategy) }
        }
    }
}

enum class Strategy {
    MOST_COMMON_BIT,
    LEAST_COMMON_BIT

}

fun main() {
    Day3.runSolution("day3.data")
}


