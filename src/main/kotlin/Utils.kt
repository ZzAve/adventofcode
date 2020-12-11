import java.io.File

fun getFile(fileName: String) = File("./src/main/resources/$fileName").readLines()

fun printMatrix(matrix: List<List<Char>>) {
    matrix.forEach { line ->
        line.forEach { c ->
            print("$c ")
        }
        println()
    }
}