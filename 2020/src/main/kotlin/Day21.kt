import org.clechasseur.adventofcode2020.Day21Data

object Day21 {
    private val input = Day21Data.input

    private val foodRegex = """^([a-z]+(?: [a-z]+)*) \(contains ([a-z]+(?:, [a-z]+)*)\)$""".toRegex()

    fun part1(): Int {
        val allFood = input.lines().map { it.toFood() }
        val allergenMap = buildAllergenMap(allFood)
        return allFood.flatMap { it.ingredients }.count { it !in allergenMap.values }
    }

    fun part2(): String {
        val allFood = input.lines().map { it.toFood() }
        val allergenMap = buildAllergenMap(allFood)
        return allergenMap.entries.sortedBy { it.key }.joinToString(",") { it.value }
    }

    private fun buildAllergenMap(allFood: List<Food>): Map<String, String> {
        val allergenMap = mutableMapOf<String, String>()
        val allAllergens = allFood.flatMap { it.allergens }.toMutableSet()
        while (allAllergens.isNotEmpty()) {
            allAllergens.associateWith { allergen ->
                val foodWithAllergen = allFood.filter { it.allergens.contains(allergen) }
                foodWithAllergen.flatMap {
                    it.ingredients
                }.distinct().filter { ingredient ->
                    foodWithAllergen.all { it.ingredients.contains(ingredient) } && ingredient !in allergenMap.values
                }
            }.filterValues { it.size == 1 }.forEach { (allergen, candidateIngredients) ->
                allergenMap[allergen] = candidateIngredients.single()
                allAllergens.remove(allergen)
            }
        }
        return allergenMap
    }

    private data class Food(val ingredients: List<String>, val allergens: List<String>)

    private fun String.toFood(): Food {
        val match = foodRegex.matchEntire(this) ?: error("Invalid food: $this")
        val (ingredients, allergens) = match.destructured
        return Food(ingredients.split(" "), allergens.split(", "))
    }
}
