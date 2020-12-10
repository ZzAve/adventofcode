package day9

import find2EntriesSumTo
import getFile

object Day9 {

    private val input = getFile("day9.data")
    private val encodedStuff = input.map { it.toLong() }
    fun solve() {
        val firstInvalidEntry = findFirstInvalidEntry() ?: -1
        findContiguousSet(firstInvalidEntry)
    }

    private fun findFirstInvalidEntry(): Long? {
        var i = 25
        while (i < encodedStuff.size) {
            //  find pair that sums to next index
            val summedPair = find2EntriesSumTo(encodedStuff[i], encodedStuff, i - 25, i)
            //  if succeeds, up index, else return index
            if (summedPair.first != -1) {
                i++
            } else {
                println("First number for which no pair exists in last 25 entries to add up to number:  ${encodedStuff[i]} (index $i)")
                return encodedStuff[i]
            }
        }
        println("YIKES! There are pairs everywhere")
        return null
    }

    private tailrec fun findContiguousSet(firstInvalidEntry: Long, startIndex: Int = 0) {
        if (startIndex == encodedStuff.size) return

        val values = mutableListOf<Long>()
        var i = startIndex + 1
        while (i < encodedStuff.size && values.sum() < firstInvalidEntry){
            values.add(encodedStuff[i])
            i++
        }

        return if (values.sum() == firstInvalidEntry){
            val slice = encodedStuff.slice(startIndex..i)
            val min = slice.minOrNull() ?: 0L
            val max = slice.maxOrNull() ?: 0L
            println("contiguous set found $values (indexes $startIndex - $i)")
            println("Min value: $min, max value: $max, sum ${min+max}")
        } else {
            findContiguousSet(firstInvalidEntry, startIndex +1 )
        }
    }
}

fun main() {
    Day9.solve()
}