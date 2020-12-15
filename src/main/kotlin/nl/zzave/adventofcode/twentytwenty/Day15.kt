package nl.zzave.adventofcode.twentytwenty

object Day15 {
    private val startingNumbers = listOf(0, 3, 1, 6, 7, 5)

    fun solve(input: List<Int> = startingNumbers): Pair<Int, Int> {

        val lastNumber = getNthSpokenNumber(input, 2020)
        println("starting with $input, the 2020th number spoken is $lastNumber")
        val thirtyMthWord = getNthSpokenNumber(input, 30_000_000)
        println("starting with $input, the 30 millionth number spoken is $thirtyMthWord")
        return lastNumber to thirtyMthWord
    }

    private fun getNthSpokenNumber(input: List<Int>, untilNthWord: Int): Int {
        // map spoken number to index (of last occurrence)
        val gaps = mutableMapOf<Int, Int>()

        for (i in 0 until input.size -1){
            gaps[input[i]] = i

        }

        var lastNumber = input.last()
        var newSpokenWord = -1
        for (turn in input.size until untilNthWord) {
            val indexOfLastEntry = gaps[lastNumber]
            newSpokenWord = if (indexOfLastEntry != null) (turn -1) - indexOfLastEntry else 0

            gaps[lastNumber] = turn -1
            lastNumber = newSpokenWord
        }

        return lastNumber

    }

    private fun get2020thSpokenNumber(input: List<Int>): Int {
        val spokenNumber = mutableListOf<Int>()
        spokenNumber.addAll(input.subList(0, input.size - 1))
        var lastNumber = input.last()
        for (turn in input.size until 2020) {
            val previousIndexOfLastNumber = spokenNumber.lastIndexOf(lastNumber)
            val newSpokenWord =
                if (previousIndexOfLastNumber == -1) 0 else spokenNumber.size - previousIndexOfLastNumber

            spokenNumber.add(lastNumber)
            lastNumber = newSpokenWord

        }
        return lastNumber
    }
}

fun main() {
    Day15.solve()
}