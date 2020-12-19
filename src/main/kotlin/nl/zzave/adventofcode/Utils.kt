package nl.zzave.adventofcode

import java.io.File

fun getFile(fileName: String) = File("./src/main/resources/$fileName").readLines()

fun <T> printMatrix(matrix: List<List<T>>) {
    matrix.forEach { line ->
        line.forEach { c ->
            print("$c ")
        }
        println()
    }
}

fun <T> prettyPrint(iterable: Iterable<T>) {
    println("{")
    iterable.forEach { println("\t $it") }
    println("}")
}

fun <S,T> prettyPrint(map : Map<S,T>){
    prettyPrint(map.entries)
}