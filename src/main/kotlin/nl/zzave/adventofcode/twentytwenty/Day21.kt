package nl.zzave.adventofcode.twentytwenty

object Day21 {
    private val rawProducts = getTwentyTwentyFile("day21.data")

    private val allergenCandidates = mutableMapOf<String, MutableSet<String>>()
    private val ingredientCandidates = mutableMapOf<String, MutableSet<String>>()

    fun solve(rawProducts: List<String> = Day21.rawProducts): Pair<Int, String> {
        val products: List<Product> = parseRawProducts(rawProducts)

        val allAllergens = products.fold(emptySet<String>()) { acc, p -> acc union p.allergens }

        val foundIngredients = mutableSetOf<String>()
        val foundAllergens = mutableSetOf<String>()

        var nrOfUnMatchedAllergens: Int

        do {
            products.forEach { p ->
                p.oldUnmatchedAllergens = p.unmatchedAllergens.toSet()
                p.oldUnmatchedIngredients = p.unmatchedIngredients.toSet()
            }

            println("Next iterations")
            for (p1 in products) {
                if (p1.oldUnmatchedAllergens.isEmpty()) continue
                if (p1.oldUnmatchedAllergens.size == p1.oldUnmatchedIngredients.size) {
                    foundAllergens.addAll(p1.oldUnmatchedAllergens)
                    foundIngredients.addAll(p1.oldUnmatchedIngredients)

                    products.forEach {
                        it.unmatchedIngredients.removeAll(p1.oldUnmatchedIngredients)
                        it.unmatchedAllergens.removeAll(p1.oldUnmatchedAllergens)
                    }

                }
                for (p2 in products) {
                    if (p1 == p2) continue
                    if (p2.oldUnmatchedAllergens.isEmpty()) continue

                    val sameIngredients = p1.oldUnmatchedIngredients intersect p2.oldUnmatchedIngredients
                    val sameAllergens = p1.oldUnmatchedAllergens intersect p2.oldUnmatchedAllergens

                    if (sameAllergens.isNotEmpty() && sameAllergens.size == sameIngredients.size) {
                        // Ideal situation n - n situation

                        foundAllergens.addAll(sameAllergens)
                        foundIngredients.addAll(sameIngredients)
                        products.forEach {
                            it.unmatchedIngredients.removeAll(sameIngredients)
                            it.unmatchedAllergens.removeAll(sameAllergens)
                        }
                    } else if (sameAllergens == p1.oldUnmatchedAllergens) {
                        p1.unmatchedIngredients.retainAll(sameIngredients)
                        print("")
                    } else if (sameAllergens == p2.oldUnmatchedAllergens) {
                        p2.unmatchedIngredients.retainAll(sameIngredients)
                        print("")
                    }
                }
            }

            println("After iteration: unmatched ingredients ${products.sumBy { it.oldUnmatchedIngredients.size }} to ${products.sumBy { it.unmatchedIngredients.size }} ")
            println("After iteration: unmatched allergens ${products.sumBy { it.oldUnmatchedAllergens.size }} to ${products.sumBy { it.unmatchedAllergens.size }} ")
            nrOfUnMatchedAllergens = products.sumBy { it.oldUnmatchedAllergens.size }
        } while (nrOfUnMatchedAllergens > 0)


        println("Found ingredients w/ allergens: $foundIngredients")
        println("Found allergens: $foundAllergens")

        val unmatchedIngredients =
            products.fold(emptySet<String>()) { acc, p -> acc union (p.ingredients subtract foundIngredients) }
        println("Unmatched (allergen free) ingredients found: $unmatchedIngredients")

        val occurrencesOfUnmatchedIngredients =
            products.fold(0) { acc, p -> acc + p.ingredients.filter { unmatchedIngredients.contains(it) }.size }

        println("Occurrences of an allergenFree ingredient: $occurrencesOfUnmatchedIngredients ")


        // Set candidates
        foundAllergens.forEach {
            allergenCandidates[it] = foundIngredients.toMutableSet()
        }

        foundIngredients.forEach {
            ingredientCandidates[it] = foundAllergens.toMutableSet()
        }

        // Filter unmatched ingredients
        products.forEach { p ->
            val filter = p.ingredients.filter { !unmatchedIngredients.contains(it) }
            p.unmatchedIngredients = filter.toMutableSet()
            p.unmatchedAllergens = p.allergens.toMutableSet()
        }


        do {
            products.forEach { p ->
                p.oldUnmatchedAllergens = p.unmatchedAllergens.toSet()
                p.oldUnmatchedIngredients = p.unmatchedIngredients.toSet()
            }

            for (p1 in products) {
                if (p1.oldUnmatchedAllergens.isEmpty()) continue
                if (p1.oldUnmatchedAllergens.size == p1.oldUnmatchedIngredients.size) {
                    updateCandidates(
                        p1.unmatchedIngredients,
                        p1.unmatchedAllergens
                    )
                }
                for (p2 in products) {
                    if (p1 == p2) continue
                    if (p2.oldUnmatchedAllergens.isEmpty()) continue

                    val sameIngredients = p1.oldUnmatchedIngredients intersect p2.oldUnmatchedIngredients
                    val sameAllergens = p1.oldUnmatchedAllergens intersect p2.oldUnmatchedAllergens

                    updateCandidates(sameIngredients, sameAllergens)
                }
            }

            allergenCandidates.forEach { entry ->
                if (entry.value.size == 1) {
                    allergenCandidates.forEach{ e ->
                        if (entry != e){
                            e.value.removeAll(entry.value)
                        }
                    }
                }
            }

            println("Another iteration: $allergenCandidates")
            nrOfUnMatchedAllergens = allAllergens.size - allergenCandidates.filter { it.value.size ==1 }.size
        } while (nrOfUnMatchedAllergens > 0)


        println("Found matching $allergenCandidates")
        val sortedAllergens = allergenCandidates.keys.sorted()
        println("Sorted keys: $sortedAllergens")
        val result = sortedAllergens.joinToString(",") { allergenCandidates[it]!!.first() }
        println("Final result: $result")
        return occurrencesOfUnmatchedIngredients to result
    }

    private fun updateCandidates(
        matchedIngredients: Set<String>,
        matchedAllergens: Set<String>
    ) {
        matchedIngredients.forEach {
            ingredientCandidates[it]!!.retainAll(matchedAllergens)
        }
        matchedAllergens.forEach {
            allergenCandidates[it]!!.retainAll(matchedIngredients)
        }
    }


    data class Product(
        val id: Int,
        val ingredients: Set<String>,
        val allergens: Set<String>,
        var unmatchedIngredients: MutableSet<String>,
        var unmatchedAllergens: MutableSet<String>,
        var oldUnmatchedIngredients: Set<String>,
        var oldUnmatchedAllergens: Set<String>
    )

    private fun parseRawProducts(rawProducts: List<String>): List<Product> =
        rawProducts.mapIndexed { index, it ->
            val matches = Regex("^(([a-z]+\\s?)+)(\\(contains\\s(.+)\\))?").find(it)
            matches!!.let { m ->
                val rawIngredients = m.groups[1]?.value?.trim()
                val rawAllergens = m.groups[4]?.value?.trim()

                val ingredients = rawIngredients?.split(' ')?.toSet() ?: emptySet()
                val allergens = rawAllergens?.split(',')?.map { it.trim() }?.toSet() ?: emptySet()

                Product(
                    id = index,
                    ingredients = ingredients,
                    allergens = allergens,
                    unmatchedIngredients = ingredients.toMutableSet(),
                    unmatchedAllergens = allergens.toMutableSet(),
                    oldUnmatchedIngredients = ingredients,
                    oldUnmatchedAllergens = allergens

                )
            }
        }
}

fun main() {
    Day21.solve()
}