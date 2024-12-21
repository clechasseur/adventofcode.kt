package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.y2022.data.Day3Data

object Day3 {
    private val input = Day3Data.input

    fun part1(): Int = input.lines().sumOf { it.toRucksack().commonType.priority }

    fun part2(): Int = input.lines().map { it.toRucksack() }.chunked(3).sumOf { it.commonType.priority }

    private data class Rucksack(val pouch1: String, val pouch2: String) {
        val allItems: String
            get() = pouch1 + pouch2
    }

    private fun String.toRucksack(): Rucksack = Rucksack(
        substring(0, length / 2),
        substring(length / 2, length)
    )

    private val Rucksack.commonType: Char
        get() = pouch1.toList().distinct().single { pouch2.contains(it) }

    private val List<Rucksack>.commonType: Char
        get() {
            require(size == 3) { "Invalid group size" }
            return this[0].allItems.toList().distinct().single {
                this[1].allItems.contains(it) && this[2].allItems.contains(it)
            }
        }

    private val Char.priority: Int
        get() = if (this in 'a'..'z') {
            this - 'a' + 1
        } else {
            this - 'A' + 27
        }
}
