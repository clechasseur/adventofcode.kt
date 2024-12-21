package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.y2022.data.Day2Data

object Day2 {
    private val input = Day2Data.input

    fun part1(): Int = input.lines().sumOf { score(it) }

    fun part2(): Int = input.lines().sumOf { score2(it) }

    private enum class Choice(val opponent: Char, val you: Char, val score: Int) {
        ROCK('A', 'X', 1),
        PAPER('B', 'Y', 2),
        SCISSORS('C', 'Z', 3);

        companion object {
            fun forOpponent(c: Char): Choice = values().first { it.opponent == c }
            fun forYou(c: Char): Choice = values().first { it.you == c }

            fun forOutcome(opponent: Choice, outcome: Char): Choice = if (outcome == 'Y') {
                opponent
            } else when (opponent) {
                ROCK -> if (outcome == 'X') SCISSORS else PAPER
                PAPER -> if (outcome == 'X') ROCK else SCISSORS
                SCISSORS -> if (outcome == 'X') PAPER else ROCK
            }
        }
    }

    private fun Choice.beats(other: Choice): Boolean? = if (other == this) null else when (this) {
        Choice.ROCK -> other == Choice.SCISSORS
        Choice.PAPER -> other == Choice.ROCK
        Choice.SCISSORS -> other == Choice.PAPER
    }

    private fun score(match: String): Int {
        val opponent = Choice.forOpponent(match[0])
        val you = Choice.forYou(match[2])
        return you.score + when (you.beats(opponent)) {
            false -> 0
            null -> 3
            true -> 6
        }
    }

    private fun score2(match: String): Int {
        val opponent = Choice.forOpponent(match[0])
        val you = Choice.forOutcome(opponent, match[2])
        return you.score + when (you.beats(opponent)) {
            false -> 0
            null -> 3
            true -> 6
        }
    }
}
