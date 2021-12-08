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
        val numberMapping = mutableMapOf<Int, Set<Char>>()

        numberMapping[1] = display.signalPattern.first { it.size == 2 }
        numberMapping[4] = display.signalPattern.first { it.size == 4 }
        numberMapping[7] = display.signalPattern.first { it.size == 3 }
        numberMapping[8] = display.signalPattern.first { it.size == 7 }

        //  dddd      0000
        // e    a    1    2
        // e    a    1    2
        //  ffff      3333
        // g    b    4    5
        // g    b    4    5
        //  cccc      6666
        val positionMapping = mutableMapOf<Position, Char>()


        // From 1 and 7, you can derive the top
        val top: Char = (numberMapping[7]!! - numberMapping[1]!!).first()
        positionMapping[Position.TOP] = top


        // From 4 and 1 you can derive center and top left
        val centerAndTopLeft: Set<Char> = (numberMapping[4]!! - numberMapping[1]!!)


        //------
        // - top
        // - top right + bottom right (1)
        // - center + top left


        // - you can derive 9 (contains all 3 elements from above + 1 additional ones)
        // - you can derive 9 (contains a 4, top, + 1 additional element, and it's not 8)
        // - you can derive bottom
        val six = display.signalPattern
            .first {
                !numberMapping.values.contains(it) &&
                        it.size == 6 &&
                        !it.containsAll(numberMapping[7]!!)

            }
        numberMapping[6] = six

        val nine = display.signalPattern
            .first {
                !numberMapping.values.contains(it) &&
                        it.size == 6 &&
                        it.containsAll(numberMapping[7]!!) &&
                        it.containsAll(centerAndTopLeft)

            }
        numberMapping[9] = nine

        val zero = display.signalPattern
            .first {
                !numberMapping.values.contains(it) &&
                        it.size == 6
            }
        numberMapping[0] = zero


        val bottomLeft = numberMapping[8]!! - numberMapping[9]!!
        check(bottomLeft.size == 1)
        positionMapping[Position.BOTTOM_LEFT] = bottomLeft.first()
        debug("$numberMapping")
        debug("$positionMapping")

        val center = numberMapping[8]!! - numberMapping[0]!!
        check(center.size == 1)
        positionMapping[Position.CENTER] = center.first()
        debug("$numberMapping")
        debug("$positionMapping")

        val topRight = numberMapping[8]!! - numberMapping[6]!!
        check(topRight.size == 1)
        positionMapping[Position.TOP_RIGHT] = topRight.first()
        debug("$numberMapping")
        debug("$positionMapping")


        // - you can derive 3 (contains 4, bottom and top, minus 1 element from four)
        val five = display.signalPattern.first {
            !numberMapping.values.contains(it) &&
                    it.size == 5 &&
                    !it.contains(positionMapping[Position.BOTTOM_LEFT]) &&
                    !it.contains(positionMapping[Position.TOP_RIGHT])
        }
        numberMapping[5] = five

        // - you can derive 3 (contains 4, bottom and top, minus 1 element from four)
        val two = display.signalPattern.first {
            !numberMapping.values.contains(it) &&
                    it.size == 5 &&
                    it.contains(positionMapping[Position.BOTTOM_LEFT])

        }
        numberMapping[2] = two

        debug("Mapped everything but 3?")
        debug("$numberMapping")
        debug("$positionMapping")

        val three = display.signalPattern.first {
            !numberMapping.values.contains(it) &&
                    it.size == 5 &&
                    !it.contains(positionMapping[Position.BOTTOM_LEFT]) &&
                    it.contains(positionMapping[Position.TOP_RIGHT])

        }
        numberMapping[3] = three



//
//        // - you can derive 3 (contains 4, bottom and top, minus 1 element from four)
//        val three = display.signalPattern.first {
//            !numberMapping.values.contains(it) &&
//                    it.size == 5 &&
//                    numberMapping.values.none { n -> n == it } &&
//                    (numberMapping[4]!! subtract it).size == 1 && // contains all but 1 elements from 4
//                    it.contains(positionMapping[Position.TOP]) &&
//                    it.contains(positionMapping[Position.BOTTOM]) &&
//                    it.size == 5
//
//        }
//        numberMapping[3] = three
//
//        // - you can derive top left
//        val topLeft = numberMapping[4]!! - numberMapping[3]!!
//        check(topLeft.size == 1)
//        positionMapping[Position.TOP_LEFT] = topLeft.first()
//
//
//        debug("$numberMapping")
//        debug("$positionMapping")
//
//        // ---------
//        // - top
//        // - top right + bottom right
//        // - bottom
//        // - center
//        // - top left
//
//
//        // you can derive 2
//        val two = display.signalPattern.first {
//            !numberMapping.values.contains(it) &&
//                    it.size == 5 &&
//                    it.contains(positionMapping[Position.BOTTOM_LEFT]) &&
//                    it.contains(positionMapping[Position.TOP]) &&
//                    it.contains(positionMapping[Position.CENTER]) &&
//                    it.contains(positionMapping[Position.BOTTOM])
//
//        }
//        numberMapping[2] = two
//
//        // you can derive bottom right
//        val bottomRight = numberMapping[1]!! - numberMapping[2]!!
//        check(bottomRight.size == 1)
//        positionMapping[Position.TOP_RIGHT] = bottomRight.first()
//
//
//        // you can derive everything
//        check(numberMapping.size == 8)
//        val five = display.signalPattern.first {
//            !numberMapping.values.contains(it) && it.size == 5
//        }
//        numberMapping[5] = five
//


        debug("Unique mapping: $numberMapping")
        // Final mapping
        return display.code.map {
            numberMapping.entries
                .first { e -> e.value == it }
                .also { debug("${it.value}: ${it.key}") }
                .key
        }.reduce { acc, i -> acc * 10 + i }
            .also { debug("${display.code} : $it") }
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


}

private fun String.sorted(): Set<Char> = toCharArray().toSet()

fun main() {
    Day8.debugMode = true
    Day8.testSolution("day8-test.data", 26, 61229)

    println("--------- NOW FOR REALS --------")

    Day8.debugMode = false
    Day8.runSolution("day8.data")
}
