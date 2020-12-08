package day8

import getFile

object Day8 {
    val bootScript = getFile("day8.data")

    fun solve() {

        var acc = runBootUntilStuck(bootScript)


        println("Accumulator value is $acc")

        val acc2 = fixBoot()
        println("Fixboot made acc: $acc2")
    }

    private fun fixBoot(): Int {

        return fixBootUntilNotStuck(
            0, mutableSetOf(), true
        )


    }

    private fun fixBootUntilNotStuck(startIndex: Int, visitedIndexed: MutableSet<Int>, canFix: Boolean): Int {
        // Try current.
        // If fails, try and swap nop with jmp and try again

        var acc = 0
        var i = startIndex
        if (i == bootScript.size - 1) {
            println("It ran completely!")
            val split2 = bootScript[i].split(' ')
            val sign2 = if (split2[1][0] == '+') 1 else -1
            val value2 = split2[1].substring(1).toInt()

            if (split2[0] == "acc") {
                acc += sign2 * value2
            }
            return acc

        }; // you made it

        if (visitedIndexed.contains(startIndex)) {
            throw RuntimeException("Nope, index already present")
        }
        visitedIndexed.add(startIndex)
        visitedIndexed

        val split = bootScript[i].split(' ')
        val sign = if (split[1][0] == '+') 1 else -1
        val value = split[1].substring(1).toInt()
        when (split[0]) {
            "nop" -> {
                i++
            }
            "acc" -> {
                acc += sign * value
                i++
            }
            "jmp" -> {
                i += sign * value

            }
        }

        return try {
            acc + fixBootUntilNotStuck(i, visitedIndexed, canFix)
        } catch (e: Exception) {
            if (canFix) {
                //  redo it.
                i = startIndex
                acc = 0
                when (split[0]) {
                    "jmp" -> {
                        i++
                    }
                    "acc" -> {
                        acc += sign * value
                        i++
                    }
                    "nop" -> {
                        i += sign * value

                    }
                }
                try {
                    acc + fixBootUntilNotStuck(i, visitedIndexed, false)
                } catch (e: Exception) {
                    visitedIndexed.remove(startIndex)
                    throw e
                }
            } else {
                visitedIndexed.remove(startIndex)
                throw e

            }
        }
    }
}


private fun runBootUntilStuck(bootScript: List<String>): Int {
    var acc = 0
    val visited = mutableSetOf<Int>()
    var i = 0
    while (true) {
        if (i == bootScript.size) {
            println("It ran completely!")
            break
        }; // you made it
        if (visited.contains(i)) break;
        visited.add(i)

        val split = bootScript[i].split(' ')

        val sign = if (split[1][0] == '+') 1 else -1
        val value = split[1].substring(1).toInt()
        when (split[0]) {
            "nop" -> i++
            "acc" -> {
                acc += sign * value
                i++
            }
            "jmp" -> {
                i += sign * value

            }
        }
    }
    return acc

}

fun main() {
    Day8.solve()
}