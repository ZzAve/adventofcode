package nl.zzave.adventofcode.twentytwenty

import java.io.File

fun getTestFile(fileName: String): List<String> = File("./src/test/resources/$fileName").readLines()
