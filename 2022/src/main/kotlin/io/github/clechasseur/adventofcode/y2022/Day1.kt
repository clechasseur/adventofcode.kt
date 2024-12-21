package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.y2022.data.Day1Data

object Day1 {
    private val input = Day1Data.input

    fun part1(): Int = input.toElves().maxOf { it.calories }

    fun part2(): Int = input.toElves().sortedByDescending { it.calories }.take(3).sumOf { it.calories }

    private data class Elf(val food: List<Int>) {
        val calories: Int
            get() = food.sum()
    }

    private fun String.toElves(): List<Elf> {
        val food = mutableListOf<Int>()
        val elves = mutableListOf<Elf>()
        for (line in lines()) {
            if (line.isEmpty()) {
                elves.add(Elf(food.toList()))
                food.clear()
            } else {
                food.add(line.toInt())
            }
        }
        if (food.isNotEmpty()) {
            elves.add(Elf(food))
        }
        return elves
    }
}
