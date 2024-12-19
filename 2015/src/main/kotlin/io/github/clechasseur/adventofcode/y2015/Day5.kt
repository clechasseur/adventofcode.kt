package io.github.clechasseur.adventofcode.y2015

import io.github.clechasseur.adventofcode.y2015.data.Day5Data

object Day5 {
    private val input = Day5Data.input

    private val vowels = setOf('a', 'e', 'i', 'o', 'u')
    private val forbidden = listOf("ab", "cd", "pq", "xy")

    fun part1(): Int = input.lines().count { it.nice1 }

    fun part2(): Int = input.lines().count { it.nice2 }

    private val String.hasThreeVowels: Boolean
        get() = filter { vowels.contains(it) }.length >= 3
    private val String.hasALetterTwiceInARow: Boolean
        get() = zipWithNext().any { it.first == it.second }
    private val String.hasForbiddenPart: Boolean
        get() = forbidden.any { this.contains(it) }

    private val String.nice1: Boolean
        get() = hasThreeVowels && hasALetterTwiceInARow && !hasForbiddenPart

    private val String.hasRepeatingPair: Boolean
        get() = zipWithNext().map {
            "${it.first}${it.second}"
        }.distinct().any {
            this.allIndexesOf(it).size >= 2
        }
    private val String.hasRepeatingLetterWithInBetween: Boolean
        get() = zipWithNext().zipWithNext().any { (a, b) -> a.first == b.second }

    private val String.nice2: Boolean
        get() = hasRepeatingPair && hasRepeatingLetterWithInBetween

    private fun String.allIndexesOf(sub: String): List<Int> {
        val indexes = mutableListOf<Int>()
        var idx = indexOf(sub, 0)
        while (idx >= 0) {
            indexes.add(idx)
            idx = indexOf(sub, idx + sub.length)
        }
        return indexes
    }
}
