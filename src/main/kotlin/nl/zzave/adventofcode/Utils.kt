package nl.zzave.adventofcode

import java.io.File

fun getFile(fileName: String) = File("./src/main/resources/$fileName").readLines()

interface Problem<T> {
    var debugMode: Boolean;

    fun log(message: Any?) = println(message)
    fun debug(message: Any?) = if (debugMode) println(message) else Unit

    fun solvePart1(input: List<String>): T
    fun solvePart2(input: List<String>): T
    fun getFile(filename: String): List<String>

    fun runSolution(filename: String) {
        val input: List<String> = getFile(filename)
        val solutionPart1: T = this.solvePart1(input)
        println("Result part 1: $solutionPart1")

        val solutionPart2 = this.solvePart2(input)
        println("Result part 2: $solutionPart2")
    }
}

fun splitByEmptyEntry(input: List<String>): List<List<String>> {
    val groups = mutableListOf<List<String>>()

    var currentGroup = mutableListOf<String>()
    input.forEach {
        if (it.isEmpty()) {
            groups.add(currentGroup)
            currentGroup = mutableListOf()
        } else {
            currentGroup.add(it)
        }
    }

    if (currentGroup.isNotEmpty()) {
        groups.add(currentGroup)
    }

    return groups

}

inline fun <reified T> transposeMatrix(
    matrix: List<List<T>>,
    default: T,
    printBeforeAfter: Boolean = false
): List<List<T>> {
    // rotate the tickets to have a list of entries instead of list of tickets
    // Transpose the matrix
    val transpose: Array<Array<T>> = Array(matrix[0].size) { Array<T>(matrix.size) { default } }
    for (i in matrix.indices) {
        for (j in matrix[i].indices) {
            transpose[j][i] = matrix[i][j]
        }
    }

    val transposedMatrix = transpose.map { it.toList() }
    if (printBeforeAfter) {
        printMatrix(matrix)
        println("---- ----- ----")
        printMatrix(transposedMatrix)
    }
    return transposedMatrix
}


fun printMatrixOfStrings(matrix: Iterable<String>) = printMatrix(matrix.map { it.map { c -> c } })

inline fun <reified T> printMatrix(matrix: Iterable<Iterable<T>>) {
    matrix.forEach { line ->
        line.forEach { c ->
            print("$c".padStart(3))
        }
        println()
    }
}
//inline fun <reified T> printMatrix(matrix: Map<Iterable<T>>) {
//
//}


fun <T> prettyPrint(iterable: Iterable<T>) {
    println("{")
    iterable.forEach { println("\t $it") }
    println("}")
}

 fun < S, T> prettyPrint(map: Map<S, T>) {
    prettyPrint(map.entries)
}
