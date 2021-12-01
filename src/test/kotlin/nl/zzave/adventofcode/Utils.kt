package nl.zzave.adventofcode

import java.io.File

fun getTestFile(fileName: String): List<String> = File("./src/test/resources/$fileName").readLines()
