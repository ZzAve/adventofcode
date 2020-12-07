package day7

import getFile

object Day7 {
    val input = getFile("day7.data")
    fun solve() {
        val myBagsColor = "shiny gold"
        val bagsMyBagFitIn = findParentBagRules(myBagsColor)
        println("A bag of color $myBagsColor fits (in)directly into ${bagsMyBagFitIn.size} different colored bags")

        val bagsInsideMyBag = findBagsInsideMyBag(myBagsColor)
        println("A bag of color $myBagsColor needs to fit $bagsInsideMyBag other bags into it 🤯")
    }

    private val parentBagCache = mutableMapOf<String, Set<String>>()
    private fun findParentBagRules(myBagsColor: String): Set<String> {
        return parentBagCache[myBagsColor] ?: determineParentBags(myBagsColor)
    }

    private fun determineParentBags(myBagsColor: String): Set<String> {
        val applicableRules = input.mapNotNull { line ->
            Regex("(.*?) bags contain.*$myBagsColor").find(line)?.let { matchResult ->
                matchResult.groups[1]?.value
                    ?.let { value ->
                        line to value
                    }
            }
        }

        val parentRules = applicableRules.map { findParentBagRules(it.second) }
        val uniqueParentRules = parentRules.reduceOrNull { acc, set -> acc.union(set) } ?: emptySet()
        val parentRuleset = applicableRules.map { it.first }.toSet().union(uniqueParentRules)
//        println("$myBagsColor - fits into ${parentRuleset.size} other colored bags. (${parentRuleset.map { it.substring(0,it.indexOf(" bags contain")) }})")

        parentBagCache[myBagsColor] = parentRuleset
        return parentRuleset
    }

    private val childBagsCache = mutableMapOf<String, Int>()
    private fun findBagsInsideMyBag(myBagsColor: String): Int {
        return childBagsCache[myBagsColor] ?: calculateNrChildBagsInside(myBagsColor)

    }

    private fun calculateNrChildBagsInside(myBagsColor: String): Int {
        val bagsInsideMyBag: List<Pair<Int, String>> = input.mapNotNull { line ->
            val applicableInput = Regex("$myBagsColor bags contain (.*)").find(line)
            applicableInput?.let { matchResult ->
                val bagContents = matchResult.groups[1]?.value
                getChildBags(bagContents)
            }
        }.flatten()

        val nrOfBagsInsideMyBag = bagsInsideMyBag.map {
            it.first * (1 + findBagsInsideMyBag(it.second))
        }.sum()
//        println("Sum $nrOfBagsInsideMyBag ($myBagsColor - $bagsInsideMyBag)")

        childBagsCache[myBagsColor] = nrOfBagsInsideMyBag
        return nrOfBagsInsideMyBag
    }

    private fun getChildBags(bagContents: String?): List<Pair<Int, String>> {
        val childrenBags = bagContents?.split(',') ?: emptyList()

        return childrenBags.mapNotNull {
            Regex("([0-9]+) (.*?) bags?").find(it)?.let { result ->
                val amount = result.groups[1]?.value?.toInt() ?: 0
                val childColor = result.groups[2]?.value ?: ""

                amount to childColor
            }
        }
    }
}

fun main() {
    Day7.solve()
}