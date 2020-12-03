import java.io.File


data class Slope(val right: Int, val down: Int)

val hillSideMap = File("./src/main/resources/day3.data").readLines()

fun main(args: Array<String>) {
    println("Hello Day 3!")

    val slopes = listOf(
        Slope(1, 1),
        Slope(3, 1),
        Slope(5, 1),
        Slope(7, 1),
        Slope(1, 2)
    )
    val encounters: List<Long> = slopes.map {
        val determineHitTreesForSlope = determineTreeEncounters(hillSideMap, it.right, it.down)
        determineHitTreesForSlope
    }

    println(encounters)
    val products: Long = encounters.reduce { acc, i -> (acc * i) }
    println("Product: $products")
}

private fun determineTreeEncounters(landscape: List<String>, right: Int, down: Int): Long {
    var treesEncountered = 0L
    var open = 0

    var rightIdx = 0;
    var downIdx = 0
    landscape.forEach { line ->
        if (downIdx % down == 0) {
            val hitsTree = line[rightIdx % line.length] == '#'
            printLine(line, rightIdx, hitsTree)

            treesEncountered += if (hitsTree) 1 else 0
            open += if (!hitsTree) 1 else 0

            rightIdx += right
        } else printLine(line, -1, false)


        // skip n lines
        downIdx++
    }

    println("Trees encountered: $treesEncountered")
    println("Open encountered: $open")
    return treesEncountered
}

private fun printLine(input: String, idx: Int, hitsTree: Boolean) {
    println(input.doIndexed { index, i ->
        if (index == idx % input.length) {
            if (hitsTree) 'X' else 'O'
        } else {
            i
        }
    })
}

private fun String.doIndexed(transform: (index: Int, Char) -> Char): String {
    var x = ""
    for (c in this.indices) {
        x += transform(c, this[c])
    }
    return x
}