package nl.zzave.adventofcode.twentytwenty

object Day25 {
    private val input: List<Long> = listOf(
        6929599, 2448427
    )

    fun solve(input: List<Long> = Day25.input) {
        val cardPublicKey = input[0]
        val doorPublicKey = input[1]

        var cardLoopSize = -1L
        var doorLoopSize = -1L

        var value = 1L
        var i = 0L
        while (cardLoopSize == -1L || doorLoopSize == -1L) {
            value *= 7
            value %= 20201227
            i++

            if (value == cardPublicKey) {
                cardLoopSize = i
            }
            if (value == doorPublicKey) {
                doorLoopSize = i
            }
        }

        println("Public keys found: $cardPublicKey ($cardLoopSize), $doorPublicKey ($doorLoopSize)")


        if (cardLoopSize > Int.MAX_VALUE) println("Loop size is too huge!")
        if (doorLoopSize > Int.MAX_VALUE) println("Loop size is too huge!")

        var doorEncryptionKey = 1L
        repeat(cardLoopSize.toInt()) {
            doorEncryptionKey *= doorPublicKey
            doorEncryptionKey %= 20201227
        }

        var cardEncryptionKey = 1L
        repeat(doorLoopSize.toInt()) {
            cardEncryptionKey *= cardPublicKey
            cardEncryptionKey %= 20201227
        }

        println("Encryption keys found: $cardEncryptionKey, $doorEncryptionKey")

    }
}

fun main() {
    Day25.solve()
}