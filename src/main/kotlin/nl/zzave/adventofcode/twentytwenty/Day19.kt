package nl.zzave.adventofcode.twentytwenty

object Day19 {
    private val messages = getTwentyTwentyFile("day19_messages.data")
    private val rules = getTwentyTwentyFile("day19_rules.data")
//    private val rules = emptyList<String>()// get2020File("day19_rules.data")


    fun solve2(messages: List<String> = Day19.messages, rules: List<String> = Day19.rules): Pair<Int, Int> {
        val ruleMap: Map<String, MessageRule> = parseRules(rules)
        val matchingMessages = findMatchingMessages(ruleMap, "0", 0, messages)

        val completeMatching: List<String> = matchingMessages.map { (index, x) ->
//            println(index)
            x.filter { it.length == index }
        }
            .flatten()

        val part1Size = completeMatching.size
        println("All matching messages for Rule #0: $part1Size ")
        completeMatching.forEach { println(it) }

        val ruleMap2 = parseRules(rules.map {
//            if (it == "0: 1 2 3 | 1 1 1") "0: 1 2| 1 0 2" else it
            if (it == "8: 42") "8: 42 | 42 8" else if (it == "11: 42 31") "11: 42 31 | 42 11 31" else it
        })

        val matchingMessages2 = findMatchingMessages(ruleMap2, "0", 0, messages)

        val completeMatching2: Set<String> = matchingMessages2.map { (index, x) ->
//            println(index)
            x.filter { it.length == index }
        }

            .flatten()
            .toSet()

        val part2Size = completeMatching2.size
        println("All matching messages for Rule #0: $part2Size ")
        completeMatching2.forEach { println(it) }

        println("$part1Size to $part2Size")
        return part1Size to part2Size
    }


    private fun findMatchingMessages(
        ruleMap: Map<String, MessageRule>,
        ruleId: String,
        startIndex: Int,
        messages: List<String>
    ): List<Pair<Int, List<String>>> {
        val rule = ruleMap[ruleId] ?: throw IllegalArgumentException("Rule #$ruleId does not exist")
        if (rule.value != null) {
            return listOf(
                (startIndex + rule.value.length) to
                        messages.filter { m -> m.startsWith(rule.value, startIndex) })
                .filter { it.second.isNotEmpty() }
        }

        // [[1], []]
        val map: List<Pair<Int, List<String>>> = rule.subRules.flatMap { subMessageRule ->
            // subMessageRule [1]
            var matchingMessages = listOf(startIndex to messages)
//            var i = 0
            for ((i, subMessageRuleId) in subMessageRule.withIndex()) {
                val newMatchingMessages = mutableListOf<Pair<Int, List<String>>>()

                for ((startIndexSub, messagesForIndex) in matchingMessages) {
                    if (subMessageRuleId == ruleId) {

                        newMatchingMessages.addAll(
                            processLoop(ruleMap, subMessageRule, i, matchingMessages, ruleId)
                        )
                    } else {
                        val matchingMessagesForSubRuleId =
                            findMatchingMessages(ruleMap, subMessageRuleId, startIndexSub, messagesForIndex)
                        newMatchingMessages.addAll(matchingMessagesForSubRuleId)
                    }
                }
                matchingMessages = newMatchingMessages
            }

            matchingMessages
        }

        return map
            .filter { it.second.isNotEmpty() }
    }

    private fun processLoop(
        ruleMap: Map<String, MessageRule>,
        messageRule: List<String>,
        i: Int,
        matchingMessages2: List<Pair<Int, List<String>>>,
        ruleId: String,
        depth: Int = 0
    ): List<Pair<Int, List<String>>> {
        // 2 options, start over, move on (wind down)

        // start over
        var matchingMessagesStartOver = matchingMessages2 + emptyList()

        for ((index, subMessageRuleId) in messageRule.withIndex()) {
            val newMatchingMessages = mutableListOf<Pair<Int, List<String>>>()

            for ((startIndexSub, messagesForIndex) in matchingMessagesStartOver) {
                if (subMessageRuleId == ruleId) {// loop one
                    if (messagesForIndex.isNotEmpty()) {
                        val loop = processLoop(
                            ruleMap,
                            messageRule,
                            i,
                            listOf(startIndexSub to messagesForIndex),
                            ruleId,
                            depth + 1
                        )
                        newMatchingMessages.addAll(loop)
                    }
                } else if (index < i) {
                    val matchingMessagesForSubRuleId =
                        findMatchingMessages(ruleMap, subMessageRuleId, startIndexSub, messagesForIndex)
                    newMatchingMessages.addAll(matchingMessagesForSubRuleId)
                } else {
                    newMatchingMessages.addAll(matchingMessagesStartOver)
                }
            }
            matchingMessagesStartOver = newMatchingMessages

        }

        // move on
        var unwindDepth = depth
        var matchingMessagesMoveOn = matchingMessages2
        while (unwindDepth > 0) {
            for ((index, subMessageRuleId) in messageRule.withIndex()) {
                if (index <= i) continue

                val newMatchingMessages = mutableListOf<Pair<Int, List<String>>>()
                for ((startIndexSub, messagesForIndex) in matchingMessagesMoveOn) {
                    val matchingMessagesForSubRuleId =
                        findMatchingMessages(ruleMap, subMessageRuleId, startIndexSub, messagesForIndex)
                    newMatchingMessages.addAll(matchingMessagesForSubRuleId)
                }
                matchingMessagesMoveOn = newMatchingMessages

            }

            unwindDepth--
        }

        return matchingMessagesStartOver + matchingMessagesMoveOn
    }


    fun solve(messages: List<String> = Day19.messages, rules: List<String> = Day19.rules): Pair<Long, Long> {
        val ruleMap: Map<String, MessageRule> = parseRules(rules)

        val sortedMessages = messages.sorted()

        // Figure out matches for rule 0
        val applicableRules = resolve(ruleMap, "0", sortedMessages)


        // Match rule 0 against messages
        val count = messages.count { applicableRules.contains(it) }
        println("nr of messages matching rule #0: $count")

        val ruleMap2 = parseRules(rules.map {
            if (it == "8: 42") "8: 42 | 42 8" else if (it == "11: 42 31") "11: 42 31 | 42 11 31" else it
        })

        val applicableRules2 = resolve(ruleMap2, "0", sortedMessages)
        val countPart2 = sortedMessages.filter { applicableRules2.contains(it) }
        println("Found rules:")
        countPart2.forEach {
            println(it)
        }
        println("after modification, nr of messages matching rule #0: $countPart2")


        return count.toLong() to countPart2.size.toLong()
    }

    private fun parseRules(rules: List<String>): Map<String, MessageRule> {
        // parse rules
        val parsedRules = rules.map { rule ->
            val split = rule.split(":")
            val ruleNr = split[0]
            var matches: Set<String>? = null
            val subRules = mutableListOf<List<String>>()
            var value: String? = null
            val ruleOptions: List<String> = split[1].split("|").map { it.trim() }
            if (ruleOptions.size == 1 && ruleOptions[0].startsWith('"')) {// single
                value = ruleOptions[0].filter { it != '"' }
                matches = setOf(value)

            } else {
                val subRuleOptions: List<List<String>> = ruleOptions
                    .map { possibility -> possibility.split(" ") }
                subRules.addAll(subRuleOptions)
            }

            MessageRule(
                id = ruleNr,
                value = value,
                matches = matches,
                matchesCount = if (matches != null) 1L else -1L,
                subRules = subRules
            )
        }

        return mapOf(*parsedRules.map { it.id to it }.toTypedArray())
    }

    private fun List<Set<String>>.findPermutations(messages: List<String>): Set<String> =
        reduce { acc: Set<String>, newerSet: Set<String> ->
            val map: List<Set<String>> = acc.map { first ->
                val subMessages = messages.filter { m -> m.doesContainPermutation(first) }
                newerSet.mapNotNull { second ->
                    val x = first + second
                    if (doMessagesContainPermutation(subMessages, x)) x else null
                }.toSet()
            }

            map.reduce { acc2, set -> acc2 union set }

        }

    private fun resolve(ruleMap: Map<String, MessageRule>, i: String, messages: List<String>): Set<String> {
        val rule = ruleMap[i] ?: throw IllegalArgumentException("Rule #$i does not exist")
        if (rule.matchesCount != -1L && rule.matches != null) {
            return rule.matches ?: throw IllegalStateException("Rule with matches should have matches (duhh)")
        } else {
            println("Process rule $i")
            // apply each subrule
            // rule = [[1,2],[3,1]]
            val ruleResults: List<Set<String>> = rule.subRules.map { subRule ->
                //subRule [1,2]
                val subRuleResults: List<Set<String>> = subRule.mapIndexed { index, it ->
                    val set = if (it == i) {
                        val resolvePrevious = resolve(ruleMap, subRule[index - 1], messages)
                        val resolveNext =
                            if (index + 1 >= subRule.size) setOf("")
                            else resolve(ruleMap, subRule[index + 1], messages)

                        var previousPreSet: Set<String>
                        var previousPostSet: Set<String>
                        var newPreSet = setOf("")
                        var newPostSet = setOf("")
                        do {
                            previousPreSet = newPreSet
                            previousPostSet = newPostSet
                            val prePermutations = listOf(previousPreSet, resolvePrevious).findPermutations(messages)
                            val postPermutations = listOf(resolveNext, previousPostSet).findPermutations(messages)

                            newPreSet = previousPreSet union prePermutations
                            newPostSet = previousPostSet union postPermutations

                            println("Iteration done pre: ${previousPreSet.size}, ${newPreSet.size} (${prePermutations.size}) post ${previousPostSet.size}, ${newPostSet.size} (${postPermutations.size}) ")
                        } while (!(previousPreSet.size == newPreSet.size && previousPostSet.size == newPostSet.size))

                        listOf(newPreSet, newPostSet).findPermutations(messages)

                    } else {
                        val set = resolve(ruleMap, it, messages)

                        val filterUnnecessaryPermutations = set.filterUnnecessaryPermutations(messages, i)
                        println("[$i] Size after filter: ${filterUnnecessaryPermutations.size}")
                        filterUnnecessaryPermutations

//                        set
                    }

                    set
                }

                val size = subRuleResults.map { it.size }
                println("[$i] Finding permutations for a subrule in #${i} based on its subrules ${size.reduce { acc, i -> acc * i }} ($size)  ")
                val permutations = subRuleResults.findPermutations(messages)

                val toSet = permutations//.filterUnnecessaryPermutations(messages)

                rule.matches = toSet
                rule.matchesCount = toSet.size.toLong()
                toSet


            }

            val size = ruleResults.map { it.size }
            println("[$i] Finding union for rule #${i} based on subrules ${size.reduce { acc, i -> acc + i }} ($size)  ")
            val permutationsTotal = ruleResults.reduce { acc: Set<String>, list: Set<String> ->
                acc union list

            }

            rule.matchesCount = permutationsTotal.size.toLong()
            rule.matches = permutationsTotal
            return rule.matches!!
        }
    }

    private fun Set<String>.filterUnnecessaryPermutations(messages: List<String>, id: String): Set<String> {
        println("[$id] Filtering unnecessary permutations (${this.size} * ${messages.size} = ${this.size * messages.size} ops)")
        return filter { permutation -> doMessagesContainPermutation(messages, permutation) }.toSet()
    }

    private val permutationCache = mutableMapOf<String, Boolean>()
    private fun doMessagesContainPermutation(messages: List<String>, permutation: String): Boolean {
        return if (permutationCache[permutation] != null) {
            permutationCache[permutation]!!
        } else {
            val messagesContainPermutation = messages.any { message ->
                message.doesContainPermutation(permutation)
            }
            permutationCache[permutation] = messagesContainPermutation
            messagesContainPermutation
        }
    }

    private fun String.doesContainPermutation(permutation: String) =
        Regex(".*$permutation.*").matches(this)

    private fun findAllPermutations(subRuleResults: List<Set<String>>) =
        subRuleResults.fold(setOf("")) { acc: Set<String>, list: Set<String> ->
            acc.flatMap { first ->
                list.map { second ->
                    first + second
                }
            }.toSet()

        }

    data class MessageRule(
        val id: String,
        val value: String?,
        var matches: Set<String>?,
        var matchesCount: Long,
        val subRules: MutableList<List<String>>
    )
}

fun main() {
    Day19.solve2()
}
