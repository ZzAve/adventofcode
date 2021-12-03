package nl.zzave.adventofcode.twentytwentyone

import nl.zzave.adventofcode.Problem


object Day3 : Problem<Int> {


    override fun solvePart1(input: List<String>): Int {
        val numbers = input.map { it.toInt(2) }
        val mostSignificantBit = numbers.maxOf { it }.takeHighestOneBit()

        val bitOccurrences = findBitOccurrences(numbers, mostSignificantBit, mutableListOf())
        println(bitOccurrences)
        println()

        val gammaRate = bitOccurrences.foldIndexed(0) { index, acc, i ->
            val isCommonBit: Int = if (i > input.size / 2.0) 1 else 0
            acc + 2.shl(bitOccurrences.size - 2 - index) * isCommonBit
        }

        println()
        val epsilonRate = bitOccurrences.foldIndexed(0) { index, acc, i ->
            val isUncommonBit: Int = if (i < input.size / 2.0) 1 else 0
            acc + 1.shl(bitOccurrences.size - 1 - index) * isUncommonBit
        }

        return gammaRate * epsilonRate
    }

    private tailrec fun findBitOccurrences(numbers: List<Int>, currentBit: Int, result: MutableList<Int>): List<Int> {
        println("Finding occurrences of bit $currentBit (${currentBit.toString(2)})")

        // Count only if nth bit (int defined by currentBit) is set.
        result += numbers.count { it and currentBit == currentBit }
        println(result)

        return if (currentBit == 1) {
            result
        } else {
            findBitOccurrences(numbers, currentBit / 2, result)
        }
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
        val occurrencesCommonBit = numbers.map { it.first }.count { it >= highestSignificantBit }
            .also { println("Occurrences common bit: $it") }

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


