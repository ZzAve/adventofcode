import java.io.File

fun getFile(fileName: String) = File("./src/main/resources/$fileName").readLines()