package nl.zzave.adventofcode.twentytwenty

import java.io.File

fun getTestFile(fileName: String) = File("./src/test/resources/$fileName").readLines()
