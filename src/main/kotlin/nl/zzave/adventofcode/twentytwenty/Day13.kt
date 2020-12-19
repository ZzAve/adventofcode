package nl.zzave.adventofcode.twentytwenty

object Day13 {
    private val inputFile = getTwentyTwentyFile("day13.data")

    fun solve(input: List<String> = inputFile): Pair<Long, Long> {
        val time = input[0].toLong()
        val activeBusses = input[1].split(",").mapNotNull {
            if (it == "x") null else it.toLong()
        }

        val (busId, departureTime) = getFirstBusToCatch(activeBusses, time)

        println("First bus that can be caught is $busId, departing at $departureTime (estimated time on busstation: $time)")
        val answerPart1 = busId * (departureTime - time)
        println("Product of busId and waiting time: $answerPart1")

        val allBusses = input[1].split(",").map {
            if (it == "x") 1L else it.toLong()
        }

        val answerPart2 = determinePart2(allBusses)
        println("First time when all busses start leaving with 1 min interval subsequently $answerPart2")


        return answerPart1 to answerPart2
    }

    private fun getFirstBusToCatch(
        activeBusses: List<Long>,
        startFrom: Long,
    ): Pair<Long, Long> {
        var departureTime = startFrom
        while (true) {
            activeBusses.forEach {
                if (departureTime % it == 0L) {
                    return it to departureTime
                }
            }
            departureTime++
        }
    }

    private fun determinePart2(allBusses: List<Long>): Long {
        val sortedList = allBusses.sorted().reversed()

        var interval = 1L
        var departureTime = interval
        val processedBusses = mutableListOf<Pair<Int,Long>>()
        sortedList.forEach { element ->
            processedBusses.add(allBusses.indexOf(element) to element)
            while (true) {
                val leaveSubsequently = doAllBussesLeaveSubsequently(processedBusses, departureTime)

                if (leaveSubsequently) {
                    interval *= element
                    break
                } else{
                    departureTime += interval
                }
            }

        }
        return departureTime
    }

    private fun doAllBussesLeaveSubsequently(concernedBusses: List<Pair<Int,Long>>, departureTime: Long): Boolean {
        concernedBusses.forEach { ( index,busId) ->
            if ((departureTime + index ) % busId != 0L) {
                return false
            }
        }
        return true
    }
}

fun main() {
    Day13.solve()
}
