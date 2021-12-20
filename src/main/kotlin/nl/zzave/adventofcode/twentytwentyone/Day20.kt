package nl.zzave.adventofcode.twentytwentyone

object Day20 : TwentyTwentyOneProblem<Long> {
    override var debugMode: Boolean = true

    override fun solvePart1(input: List<String>): Long {
        val (algorithm, image) = parseInput(input)

        prettyPrint(image, padStart = 2, absent = ".")
        val iterations = 2
        val (enhancedImage, inverted) = enhance(image, algorithm, iterations, false)
        if (inverted) logln("Resulting image is inverted. This means infinite pixels are lit 🤔")

        prettyPrint(enhancedImage, padStart = 2, absent = ".")
        val litPixels = enhancedImage.size.toLong()
        logln("Lit pixels after $iterations iterations: $litPixels")
        return litPixels
    }

    private fun enhance(
        image: Set<Coord>,
        algorithm: Set<Int>,
        iterations: Int,
        inverted: Boolean
    ): Pair<Set<Coord>, Boolean> {
        if (iterations <= 0) return image to inverted
        val newImage = mutableSetOf<Coord>()
        for (y in (image.minOf { it.y } - 1)..(image.maxOf { it.y } + 1)) {
            for (x in (image.minOf { it.x } - 1)..(image.maxOf { it.x } + 1)) {
                //take surrounding pixels
                var id = ""
                (y - 1..y + 1).forEach { selectedY ->
                    (x - 1..x + 1).forEach { selectedX ->
                        id += if (!inverted && image.contains(Coord(selectedX, selectedY))) {
                            '1'
                        } else if (inverted && !image.contains(Coord(selectedX, selectedY))) {
                            '1'
                        } else {
                            '0'
                        }
                    }
                }
                val algorithmIndex = id.toInt(2)
                if (algorithm.contains(algorithmIndex)) {
                    newImage.add(Coord(x, y))
                }
            }
        }

        val invertedImage = newImage.toSet()
        val shouldInvert = algorithm.contains(0) && !inverted
        if (shouldInvert) {
            prettyPrint(newImage, padStart = 2, absent = ".")
            //invert!
            newImage.clear()
            for (y in (invertedImage.minOf { it.y })..(invertedImage.maxOf { it.y })) {
                for (x in (invertedImage.minOf { it.x })..(invertedImage.maxOf { it.x })) {
                    val coord = Coord(x, y)
                    if (!invertedImage.contains(coord)) {
                        newImage.add(coord)
                    }
                }
            }
        }

        val newInverted = shouldInvert


        logln("Done. ${iterations - 1} iterations to go")
        if(debugMode) prettyPrint(newImage, padStart = 2, present= if(newInverted) "O" else "X", absent = ".")
        return enhance(newImage, algorithm, iterations - 1, newInverted)

    }

    private fun parseInput(input: List<String>): Pair<Set<Int>, Set<Coord>> {
        val algorithm = input.takeWhile { it.isNotBlank() }
            .also { check(it.size == 1) { "Algorithm should be a single line" } }
            .let { it[0] }
            .also { check(it.length == 512) { "Algorithm should be 512 pieces" } }
            .mapIndexedNotNull { index, value ->
                if (value == '#') {
                    index
                } else null
            }.toSet()

        val startImage = input.takeLastWhile { it.isNotBlank() }
            .flatMapIndexed { yIndex, line ->
                val flatMapIndexed: List<Coord?> = line.mapIndexed { xIndex, value ->
                    if (value == '#') {
                        Coord(xIndex, yIndex)

                    } else null
                }
                flatMapIndexed
            }
            .mapNotNull { it }
            .toSet()

        return algorithm to startImage
    }

    override fun solvePart2(input: List<String>): Long {
        val (algorithm, image) = parseInput(input)

        prettyPrint(image, padStart = 2, absent = ".")
        val iterations = 50
        val (enhancedImage, inverted) = enhance(image, algorithm, iterations, false)
        if (inverted) logln("Resulting image is inverted. This means infinite pixels are lit 🤔")

        prettyPrint(enhancedImage, padStart = 2, absent = ".")
        val litPixels = enhancedImage.size.toLong()
        logln("Lit pixels after $iterations iterations: $litPixels")
        return litPixels
    }
}

fun main() {
    Day20.testSolution("day20-test.data", 35, 3351)
    println("--------- NOW FOR REALS --------")
    Day20.runSolution("day20.data")
}
