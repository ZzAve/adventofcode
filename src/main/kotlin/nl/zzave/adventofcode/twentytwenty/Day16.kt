package nl.zzave.adventofcode.twentytwenty

import nl.zzave.adventofcode.getFile
import nl.zzave.adventofcode.prettyPrint
import nl.zzave.adventofcode.printMatrix

object Day16 {
    data class Rule(
        val ruleName: String,
        val ranges: List<IntRange>
    )

    private val myTicket = getFile("twentytwenty/day16_my_ticket.data")
    private val nearbyTickets = getFile("twentytwenty/day16_nearby_tickets.data")

    private val rules = getFile("twentytwenty/day16_rules.data")


    /* Ticket example
    .--------------------------------------------------------.
    | ????: 101    ?????: 102   ??????????: 103     ???: 104 |
    |                                                        |
    | ??: 301  ??: 302             ???????: 303      ??????? |
    | ??: 401  ??: 402           ???? ????: 403    ????????? |
    '--------------------------------------------------------'
    */

    fun solve(
        myTicket: List<String> = Day16.myTicket,
        nearbyTickets: List<String> = Day16.nearbyTickets,
        rules: List<String> = Day16.rules
    ): Pair<Int, Long> {

        // parse rules
        val parsedRules = parseRules(rules)
        // parse nearbyTickets
        val parsedTickets = parseTickets(nearbyTickets)

        val ticketsWithInvalidNumbers = findInvalidNumbersOnTickets(parsedTickets, parsedRules)
        val sum = ticketsWithInvalidNumbers.fold(0) { acc, ticket -> acc + ticket.second.sum() }
        println("Sum of invalid number found on tickets: $sum")

        val looselyValidTickets: List<List<Int>> = ticketsWithInvalidNumbers
            .filter { it.second.isEmpty() }
            .map { it.first }

        val entries: Array<IntArray> = transposeTicketsToEntries(looselyValidTickets)
        val (ruleMappings, entryMappings) = findAllPossibleRuleEntryPossibilities(entries, parsedRules)


        val finalRuleMap = determineFinalRuleMapping(entryMappings, ruleMappings)
        print("FinalRuleMap: ")
        prettyPrint(finalRuleMap)

        val myParsedTicket = parseTickets(myTicket)[0]
        println("MyParsedTicket: $myParsedTicket")

        val filter = finalRuleMap.filter {
            it.key.ruleName.startsWith("departure")
        }
        print("filter: ")
        prettyPrint(filter)

        val map = filter.map {
            myParsedTicket[it.value]
        }
        println("Map: $map")
        val product = map.fold(1L) { acc, i -> acc * i }

        println("product of departure fields: $product")
        return sum to product
    }

    private fun determineFinalRuleMapping(
        entryMappings: MutableMap<Int, MutableList<Rule>>,
        ruleMappings: MutableMap<Rule, MutableList<Int>>
    ): MutableMap<Rule, Int> {
        val finalRuleMap = mutableMapOf<Rule, Int>()
        var hasProcessed: Boolean
        do {
            hasProcessed = false

            // Check if there are rules with only a single entry
            // Why can't I apply this too?
            //            for (ruleMapping in ruleMappings) {
            //                if (ruleMapping.value.size == 1) {
            //                    val applicableEntry = ruleMapping.value[0]
            //                    val applicableRule = ruleMapping.key
            //                    finalRuleMap[applicableRule] = applicableEntry
            //
            //                    ruleMappings.forEach{
            //                        it.value.remove(applicableEntry)
            //                    }
            //                    entryMappings.forEach {
            //                        it.value.remove(applicableRule)
            //                    }
            //                    hasProcessed = true;
            //                }
            //            }


            for (entryMapping in entryMappings) {
                if (entryMapping.value.size == 1) {
                    val applicableRule = entryMapping.value[0]
                    val applicableEntry = entryMapping.key
                    finalRuleMap[applicableRule] = applicableEntry

                    entryMappings.forEach {
                        it.value.remove(applicableRule)
                    }
                    ruleMappings.forEach {
                        it.value.remove(applicableEntry)
                    }
                    hasProcessed = true
                }
            }


        } while (hasProcessed)
        println("Are there any unmapped entries left? ${ruleMappings.filter { it.value.isNotEmpty() }} , ${entryMappings.filter { it.value.isNotEmpty() }} ")
        return finalRuleMap
    }

    private fun findAllPossibleRuleEntryPossibilities(
        entries: Array<IntArray>,
        parsedRules: List<Rule>
    ): Pair<MutableMap<Rule, MutableList<Int>>, MutableMap<Int, MutableList<Rule>>> {
        // Rule to n-th position
        val ruleMappings = mutableMapOf<Rule, MutableList<Int>>()
        // Entry (n-th position) to Rule
        val entryMappings = mutableMapOf<Int, MutableList<Rule>>()

        // For each entry, denote each Rule which is completely valid
        entries.forEachIndexed { index, entry ->
            parsedRules.forEach { rule ->
                val all = entry.all {
                    rule.ranges.any { range ->
                        it in range
                    }

                }
                if (all) {
                    if (ruleMappings[rule] == null) ruleMappings[rule] = mutableListOf(index)
                    else ruleMappings[rule]?.add(index)

                    if (entryMappings[index] == null) entryMappings[index] = mutableListOf(rule)
                    else entryMappings[index]?.add(rule)
                }
            }
        }
        return Pair(ruleMappings, entryMappings)
    }

    private fun transposeTicketsToEntries(looselyValidTickets: List<List<Int>>): Array<IntArray> {
        // rotate the tickets to have a list of entries instead of list of tickets
        // Transpose the matrix
        val transpose: Array<IntArray> = Array(looselyValidTickets[0].size) { IntArray(looselyValidTickets.size) }
        for (i in looselyValidTickets.indices) {
            for (j in looselyValidTickets[i].indices) {
                transpose[j][i] = looselyValidTickets[i][j]
            }
        }
//        nl.zzave.adventofcode.printMatrix(looselyValidTickets)
//        println("---- ----- ----")
//        nl.zzave.adventofcode.printMatrix(transpose)
        return transpose
    }

    private fun findInvalidNumbersOnTickets(
        parsedTickets: List<List<Int>>,
        parsedRules: List<Rule>
    ): List<Pair<List<Int>, List<Int>>> = parsedTickets.map { ticket ->
        ticket to ticket.filter { field ->
            parsedRules.none { rule ->
                rule.ranges.any { field in it }
            }
        }
    }

    private fun parseTickets(tickets: List<String>) = tickets.map { it.split(",").map { i -> i.toInt() } }


    private fun parseRules(rules: List<String>): List<Rule> = rules.map { it ->
        val split = it.split(':')
        val ruleName = split[0]
        val split1 = split[1].split(Regex(" or "))

        val ranges: List<IntRange> = split1.map { range ->
            val dash = range.indexOf("-")
            val lower = range.substring(0, dash).trim()
            val upper = range.substring(dash + 1).trim()
            lower.toInt()..upper.toInt()
        }

        Rule(ruleName, ranges)
    }


    private fun printMatrix(matrix: Array<IntArray>) {
        printMatrix(matrix.map { it.toList() }.toList())
    }
}

fun main() {
    Day16.solve()
}