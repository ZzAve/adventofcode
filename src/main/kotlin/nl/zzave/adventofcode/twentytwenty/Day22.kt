package nl.zzave.adventofcode.twentytwenty

import nl.zzave.adventofcode.splitByEmptyEntry

object Day22 {
    private val deckInput = getTwentyTwentyFile("day22.data")
    fun solve(deckInput: List<String> = Day22.deckInput): Pair<List<Int>, List<Int>> {
        val players = parseDeckInput(deckInput)
        println("Initial state:")
        players.forEach { println(it) }

        val playCombat = playCombat(players)

        val results = reduceByReverseIndexValueProduct(playCombat)

        println("Player scores (starting with Player 1): $results")

        val recursiveCombatResults = playRecursiveCombat(players[0].cards.toMutableList(), players[1].cards.toMutableList())
        val recursiveResults = reduceByReverseIndexValueProduct(recursiveCombatResults)

        println("Player scores (starting with Player 1): $recursiveResults")

        println("--------")
        println("Combat: Player scores (starting with Player 1): $results")
        println("RecursiveCombat: Player scores (starting with Player 1): $recursiveResults")
        return results to recursiveResults
    }

    private fun reduceByReverseIndexValueProduct(recursiveCombatResults: List<List<Int>>) =
        recursiveCombatResults.map { p ->
            var sum = 0
            p.forEachIndexed { index, i ->
                val i1 = i * (p.size - index)
                sum += i1
            }

            sum
        }

    private fun playCombat(players: List<Player>): List<List<Int>> {
        val playerCards = players.map { it.cards.toMutableList() }
        var i = 0
        var cards: List<Int> = emptyList()
        while (playerCards.all { it.size > 0 }) {
            println("-- Round ${i + 1} --")
            players.forEachIndexed { index, it ->
                println("${it.id}'s deck: ${playerCards[index]}")
            }

            cards = playerCards.map {
                it.removeAt(0)
            }
            cards.forEachIndexed { index, i ->
                println("${players[index].id} plays $i")
            }

            val sortedDescending = cards.sortedDescending()

            if (sortedDescending == cards) {
                // 1 wins
                playerCards[0].addAll(sortedDescending)
                println("Player 1 wins the round!")
            } else {
                // 2 wins
                playerCards[1].addAll(sortedDescending)
                println("Player 2 wins the round!")
            }

            println()
            i++
        }

        println("== Post-game results ==")
        players.forEach {
            println("${it.id}'s deck: ${it.cards}")
        }

        return playerCards
    }


    private fun playRecursiveCombat(player1Deck1: List<Int>, player2Deck2: List<Int>, game: Int = 1): List<List<Int>> {
        val player1Deck = player1Deck1.toMutableList()
        val player2Deck = player2Deck2.toMutableList()

        println()
        println("== Game $game ==")

        var i = 0
        val configurations: MutableSet<List<Int>> = mutableSetOf()
        var drawnCard1: Int
        var drawnCard2: Int
        while (player1Deck.isNotEmpty() && player2Deck.isNotEmpty()) {
            if (configurations.contains(player1Deck)) {
                // player 1 wins
                println("Infinite rule applies. This setup has been found before...")
                println("Player 1 wins game $game")
                return listOf(player1Deck, player2Deck)
            }

            configurations.add(player1Deck.toList())


            println("-- Round ${i + 1} --")
            println("Player 1's deck: $player1Deck")
            println("Player 2's deck: $player2Deck")

            drawnCard1 = player1Deck.removeAt(0)
            println("Player 1 plays $drawnCard1")
            drawnCard2 = player2Deck.removeAt(0)
            println("Player 2 plays $drawnCard2")


            if (player1Deck.size >= drawnCard1 && player2Deck.size >= drawnCard2) {
                // play sub game

                println("Playing a subgame to determine the winner ...")
                val result = playRecursiveCombat(player1Deck.take(drawnCard1), player2Deck.take(drawnCard2), game + 1)
                println("... anyway, back to game $game")
                if (result[0].isEmpty()) {
                    //player 2 won
                    println("Player 2 won round $i of game $game")
                    player2Deck.add(drawnCard2)
                    player2Deck.add(drawnCard1)

                } else {
                    //player 1 won
                    println("Player 1 won round $i of game $game")
                    player1Deck.add(drawnCard1)
                    player1Deck.add(drawnCard2)
                }
            } else if (drawnCard1 > drawnCard2) {
                // 1 wind
                player1Deck.add(drawnCard1)
                player1Deck.add(drawnCard2)
                println("Player 1 wins the round of game $game!")
            } else {
                //2 wins
                player2Deck.add(drawnCard2)
                player2Deck.add(drawnCard1)
                println("Player 2 wins the round of game $game!")
            }


            println()
            i++
        }

        println("== Post-game results ==")
        println("Player 1's deck: $player1Deck")
        println("Player 2's deck: $player2Deck")


        return listOf(player1Deck, player2Deck)
    }


    private fun parseDeckInput(deckInput: List<String>): List<Player> {
        val splitByEmptyEntry: List<List<String>> = splitByEmptyEntry(deckInput)

        return splitByEmptyEntry.map {
            val playedID = it[0]
            val cards = it.subList(1, it.size).map { cardNr ->
                cardNr.toInt()
            }

            Player(id = playedID, cards = cards.toMutableList())
        }
    }

    data class Player(
        val id: String,
        val cards: MutableList<Int>
    )
}

fun main() {
    Day22.solve()
}