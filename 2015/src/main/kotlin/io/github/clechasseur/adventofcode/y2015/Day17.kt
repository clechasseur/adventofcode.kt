package io.github.clechasseur.adventofcode.y2015

import io.github.clechasseur.adventofcode.y2015.data.Day17Data

object Day17 {
    private val input = Day17Data.input

    fun part1(): Int = input.possibilities(150).size

    fun part2(): Int {
        val possible = input.possibilities(150).sorted()
        return possible.takeWhile { it == possible.first() }.size
    }

    private fun List<Int>.possibilities(target: Int): List<Int> = sortedDescending().sortedPossibilities(target, 0)

    private fun List<Int>.sortedPossibilities(target: Int, count: Int): List<Int> = when {
        target < 0 -> emptyList()
        target == 0 -> listOf(count)
        else -> indices.map { i ->
            subList(i + 1, size).sortedPossibilities(target - this[i], count + 1)
        }.fold(listOf()) { acc, l -> acc + l }
    }
}
