package nl.zzave.adventofcode.twentytwentyone

import nl.zzave.adventofcode.Problem
import nl.zzave.adventofcode.getFile

fun getTwentyTwentyOneFile(filename: String) = getFile("twentytwentyone/$filename")

interface TwentyTwentyOneProblem<T> : Problem<T> {
    override fun getFile(filename: String): List<String>{
        return getTwentyTwentyOneFile(filename)
    }
}
