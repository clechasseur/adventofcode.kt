package io.github.clechasseur.adventofcode2021

import io.github.clechasseur.adventofcode2021.data.Day3Data

object Day3 {
    private val data = Day3Data.data

    fun part1(): Int {
        val gamma = data.lines().first().indices.map { i ->
            data.lines().map { it[i] }.groupBy { it }.mapValues { it.value.size }.toList().maxByOrNull { it.second }!!.first
        }.joinToString("")
        val epsilon = gamma.map { when (it) {
            '0' -> '1'
            '1' -> '0'
            else -> error("Wrong binary digit: $it")
        } }.joinToString("")
        return gamma.toInt(radix = 2) * epsilon.toInt(radix = 2)
    }

    fun part2(): Int {
        val oxygen = rating(data.lines(), 0, '1')
        val co2 = rating(data.lines(), 0, '0')
        return oxygen.toInt(radix = 2) * co2.toInt(radix = 2)
    }

    private fun rating(nums: List<String>, pos: Int, tiebreaker: Char): String = when (nums.size) {
        1 -> nums.first()
        else -> {
            val digit = commonDigit(nums, pos, tiebreaker)
            rating(nums.filter { it[pos] == digit }, pos + 1, tiebreaker)
        }
    }

    private fun commonDigit(nums: List<String>, pos: Int, tiebreaker: Char): Char {
        val counts = nums.map { it[pos] }.groupBy { it }.mapValues { it.value.size }.toList().sortedBy { it.second }
        return when (counts[0].second) {
            counts[1].second -> tiebreaker
            else -> counts[tiebreaker.toString().toInt()].first
        }
    }
}
