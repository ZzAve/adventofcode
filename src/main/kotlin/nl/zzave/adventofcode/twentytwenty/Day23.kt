package nl.zzave.adventofcode.twentytwenty

object Day23 {
    private const val PUZZLE_INPUT = "586439172"

    fun solve(puzzleInput: String = PUZZLE_INPUT): Pair<String, Long> {
        val initialSetup = parseInput(puzzleInput)
        println("Initial state: $initialSetup")

        val part1Setup = mutableMapOf<Long, Long>() // linked list of value, next value
        for ((i, j) in initialSetup.zipWithNext()) {
            part1Setup[i] = j
        }
        part1Setup[initialSetup.last()] = initialSetup.first()

        val (cups, currentCup) = playCups(
            cups = part1Setup,
            currentCup = initialSetup.first(),
            maxValue = initialSetup.maxOrNull()!!,
            roundsToPlay = 100
        )
        println("---")
        printCups(cups, currentCup)
        println("--")

        // Concatonate ints to string, starting at the first one after "1"
        var nextCup = cups[1]!!
        var result = ""
        while (nextCup != 1L) {
            result += nextCup
            nextCup = cups[nextCup]!!
        }

        println()
        println(" -- Part 2 --")
        val part2Setup = mutableMapOf<Long, Long>() // linked list of value, next value
        for ((i, j) in initialSetup.zipWithNext()) {
            part2Setup[i] = j
        }

        val maxValue = initialSetup.maxOrNull()!!
        for ((i, j) in (maxValue +1 .. 1_000_000L).zipWithNext()) {
            part2Setup[i] = j
        }
        part2Setup[initialSetup.last()] = maxValue + 1
        part2Setup[1_000_000L] = initialSetup.first()
        printCups(part2Setup, initialSetup.first())

        val (cups2, _) = playCups(
            cups = part2Setup,
            currentCup = initialSetup.first(),
            maxValue = 1_000_000L,
            roundsToPlay = 10_000_000
        )

        println("---")
        printCups(cups2, currentCup)

        val firstCup = cups2[1]!!
        val secondCup = cups2[firstCup]!!

        val product = 1L * firstCup * secondCup

        println("Labels found: $firstCup and $secondCup")
        println("Product of labels of after 10_000_000 iterations: $product")

        println("Part1: $result")
        println("Part2: $product")
        return result to product
    }

    private fun parseInput(puzzleInput: String): List<Long> {
        return puzzleInput.map { it.toString().toLong() }
    }

    private tailrec fun playCups(
        cups: MutableMap<Long, Long>,
        currentCup: Long,
        maxValue: Long,
        roundsToPlay: Int = 100
    ): Pair<MutableMap<Long, Long>, Long> {
        if (roundsToPlay < 1) return cups to currentCup

        val printThisRound = roundsToPlay % 10_000 == 0
        if (printThisRound) println("-- Rounds left $roundsToPlay --")
        if (printThisRound) printCups(cups, currentCup)

        val pick1 = cups[currentCup]!!
        val pick2 = cups[pick1]!!
        val pick3 = cups[pick2]!!
        if (printThisRound)println("pick up: ${listOf(pick1, pick2, pick3)}")


        // determine destination cup
        var potentialDestinationCup = currentCup
        do {
            potentialDestinationCup -= 1

            if (potentialDestinationCup < 1){
                potentialDestinationCup  = maxValue
            }
        } while (listOf(pick1, pick2, pick3).contains(potentialDestinationCup))

        if (printThisRound) println("destination: $potentialDestinationCup")

        val oldPick3Destination = cups[pick3]!!
        val newPick3Destination = cups[potentialDestinationCup]!!

        cups[currentCup] = oldPick3Destination
        cups[potentialDestinationCup] = pick1
        cups[pick3] = newPick3Destination

        return playCups(cups, cups[currentCup]!!, maxValue, roundsToPlay - 1)
    }

    private fun printCups(cups: Map<Long, Long>, currentCup: Long, maxLength : Int = 10) {
        print("cups: [ ($currentCup) ")
        var nextCup: Long = cups[currentCup]!!
        var leftLength = maxLength
        while (nextCup != currentCup && leftLength > 0) {
            print("$nextCup ")
            nextCup = cups[nextCup]!!
            leftLength -= 1
        }
        if (nextCup != currentCup) print ("... ")
        println("]")
    }
}

fun main() {
    Day23.solve()
}