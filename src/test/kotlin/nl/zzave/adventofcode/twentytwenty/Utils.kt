package nl.zzave.adventofcode.twentytwenty

import java.io.File

fun getFile(fileName: String) = File("./src/test/resources/$fileName").readLines()
