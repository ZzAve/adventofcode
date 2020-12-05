import java.io.File

object Day5 {
    private val boardingPassScans = File("./src/main/resources/day5.data").readLines()

    fun solve() {
        val seatIds = boardingPassScans.map { calculateSeatId(it) }
        println("Highest seatId: ${seatIds.maxOrNull()}")

        // Sort, then find gap of 1
        val sortedSeatIds = seatIds.sorted()
        for (i in 1 until sortedSeatIds.size){
            if (sortedSeatIds[i] - sortedSeatIds[i-1] == 2) {
                println("My seatIds is ${sortedSeatIds[i] -1 }. Between ${sortedSeatIds[i-1]} and ${sortedSeatIds[i]}")
                break;
            }
        }

        println("Let's fasten that seatbelt.")
    }

    private fun calculateSeatId(it: String): Int {
        // split to row and seat
        // convert to binary
        val row = it.substring(0, 7)
            .replace('F', '0')
            .replace('B', '1')

        val seat = it.substring(7)
            .replace('L', '0')
            .replace('R', '1')

        // parse binary to int
        val rowNr = row.toInt(2)
        val seatNr = seat.toInt(2)

        // calculate seatID
        return rowNr * 8 + seatNr
    }
}

fun main(args: Array<String>) {
    Day5.solve()
}