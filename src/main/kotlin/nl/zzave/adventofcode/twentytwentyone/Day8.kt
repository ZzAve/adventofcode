package nl.zzave.adventofcode.twentytwentyone

object Day8 : TwentyTwentyOneProblem<Long> {
    override var debugMode: Boolean = false

    override fun solvePart1(input: List<String>): Long {
        val displays = parseDisplayInput(input)



        return displays.sumOf { it.code.count { c -> setOf(2, 3, 4, 7).contains(c.size) } }.toLong()

    }

    override fun solvePart2(input: List<String>): Long {
        val displays = parseDisplayInput(input)

        val displayValues = displays.map {
            findMapping(it)

        }

        return displayValues.sumOf { it.toLong() }
    }

    private fun findMapping(display: Display): Int {

        debug("----------------")
        debug("Finding mapping for $display")
        val numberMapping = mutableMapOf<Int, Set<Char>>()
        val one = display.signalPattern.first { it.size == 2 }.also { numberMapping[1] = it }
        val four = display.signalPattern.first { it.size == 4 }.also { numberMapping[4] = it }
        val seven = display.signalPattern.first { it.size == 3 }.also { numberMapping[7] = it }
        val eight = display.signalPattern.first { it.size == 7 }.also { numberMapping[8] = it }

        //  dddd
        // e    a
        // e    a
        //  ffff
        // g    b
        // g    b
        //  cccc
        val positionMapping = mutableMapOf<Position, Char>()

        // From 1 and 7, you can derive the top
        val top: Char = (seven - one).first()
        positionMapping[Position.TOP] = top
        debug("$numberMapping")
        debug("$positionMapping")

        // From 4 and 1 you can derive center and top left
        val centerAndTopLeft: Set<Char> = (four - one)

        // Six, nine, and zero al have 6 six signals
        val six = display.signalPattern
            .first {
                !numberMapping.values.contains(it) &&
                        it.size == 6 &&
                        !it.containsAll(seven)

            }.also { numberMapping[6] = it }

        val nine = display.signalPattern
            .first {
                !numberMapping.values.contains(it) &&
                        it.size == 6 &&
                        it.containsAll(seven) &&
                        it.containsAll(centerAndTopLeft)

            }.also { numberMapping[9] = it }

        val zero = display.signalPattern
            .first {
                !numberMapping.values.contains(it) &&
                        it.size == 6
            }.also { numberMapping[0] = it }


        val bottomLeft = eight - nine
        check(bottomLeft.size == 1)
        positionMapping[Position.BOTTOM_LEFT] = bottomLeft.first()

        val center = eight - zero
        check(center.size == 1)
        positionMapping[Position.CENTER] = center.first()

        val topRight = eight - six
        check(topRight.size == 1)
        positionMapping[Position.TOP_RIGHT] = topRight.first()
        debug("$numberMapping")
        debug("$positionMapping")


        // five, two, and three all have 5 signals
        display.signalPattern.first {
            !numberMapping.values.contains(it) &&
                    it.size == 5 &&
                    !it.contains(positionMapping[Position.BOTTOM_LEFT]) &&
                    !it.contains(positionMapping[Position.TOP_RIGHT])
        }.also { numberMapping[5] = it }


        display.signalPattern.first {
            !numberMapping.values.contains(it) &&
                    it.size == 5 &&
                    it.contains(positionMapping[Position.BOTTOM_LEFT])
        }.also { numberMapping[2] = it }

        debug("Mapped everything but 3?")
        check(numberMapping.size == 9)
        check(numberMapping[3] == null)
        debug("$numberMapping")
        debug("$positionMapping")

        display.signalPattern.first {
            !numberMapping.values.contains(it) &&
                    it.size == 5 &&
                    !it.contains(positionMapping[Position.BOTTOM_LEFT]) &&
                    it.contains(positionMapping[Position.TOP_RIGHT])

        }.also { numberMapping[3] = it }

        debug("Unique mapping: $numberMapping")
        // Final mapping
        return display.code.map {
            numberMapping.entries
                .first { e -> e.value == it }
                .also { debug("${it.value}: ${it.key}") }
                .key
        }.reduce { acc, i -> acc * 10 + i }
            .also { d -> log("${display.code.map{ it.joinToString("")}} : $d") }
    }

    private fun parseDisplayInput(input: List<String>): List<Display> = input
        .map { line ->
            val (signals, code) = line.split("|")
            val signalPattern = signals.trim().split(" ").map { it.sorted() }
            val fourDigitCode = code.trim().split(" ").map { it.sorted() }
            Display(signalPattern, fourDigitCode).also { debug("$it") }
        }


    data class Display(
        val signalPattern: List<Set<Char>>,
        val code: List<Set<Char>>


    )

    enum class Position {
        TOP, TOP_LEFT, TOP_RIGHT, CENTER, BOTTOM_LEFT, BOTTOM_RIGHT, BOTTOM
    }


    private fun String.sorted(): Set<Char> = toCharArray().toSet()
}


fun main() {
    Day8.debugMode = true
    Day8.testSolution("day8-test.data", 26, 61229)

    println("--------- NOW FOR REALS --------")

    Day8.debugMode = false
    Day8.runSolution("day8.data")
}
