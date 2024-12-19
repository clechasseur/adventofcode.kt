package io.github.clechasseur.adventofcode.y2015

import kotlin.math.max

object Day15 {
    private val input = mapOf(
        // <REDACTED>
    )

    fun part1(): Int = recipes(input.keys, 100).maxOf { it.score }

    fun part2(): Int = recipes(input.keys, 100).filter { it.calories == 500 }.maxOf { it.score }

    private data class Ingredient(val capacity: Int, val durability: Int, val flavor: Int, val texture: Int, val calories: Int)

    private class Recipe(val ingredients: Map<String, Int>) {
        val score: Int
            get() {
                var capacity = 0
                var durability = 0
                var flavor = 0
                var texture = 0
                ingredients.forEach { (name, teaspoons) ->
                    capacity += teaspoons * input[name]!!.capacity
                    durability += teaspoons * input[name]!!.durability
                    flavor += teaspoons * input[name]!!.flavor
                    texture += teaspoons * input[name]!!.texture
                }
                return max(capacity, 0) * max(durability, 0) * max(flavor, 0) * max(texture, 0)
            }

        val calories: Int
            get() = ingredients.asSequence().sumOf { (name, teaspoons) ->
                input[name]!!.calories * teaspoons
            }
    }

    private fun recipes(names: Collection<String>, teaspoons: Int): Sequence<Recipe> {
        if (names.size == 1) {
            return sequenceOf(Recipe(mapOf(names.first() to teaspoons)))
        }

        return (teaspoons downTo 0).asSequence().flatMap { i ->
            recipes(names.drop(1), teaspoons - i).map { subRecipe ->
                Recipe(subRecipe.ingredients + mapOf(names.first() to i))
            }
        }
    }
}
