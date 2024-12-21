package io.github.clechasseur.adventofcode2021

import io.github.clechasseur.adventofcode2021.data.Day4Data

object Day4 {
    private val data = Day4Data.data

    fun part1(): Int {
        val game = Game(data.lines())
        for (draw in game.draws) {
            game.cards.forEach { it.mark(draw) }
            val winner = game.cards.find { it.won }
            if (winner != null) {
                return winner.score(draw)
            }
        }
        error("No winner!")
    }

    fun part2(): Int {
        val game = Game(data.lines())
        for (draw in game.draws) {
            game.cards.forEach { it.mark(draw) }
            val losers = game.cards.filter { !it.won }
            if (losers.size == 1) {
                val loser = losers.first()
                for (loserDraw in game.draws) {
                    loser.mark(loserDraw)
                    if (loser.won) {
                        return loser.score(loserDraw)
                    }
                }
            }
        }
        error("No winner!")
    }

    private val lineRegex = """^(..) (..) (..) (..) (..)$""".toRegex()

    private class Square(val value: Int, var marked: Boolean = false)

    private class Card(lines: List<String>) {
        val squares: List<List<Square>> = lines.map { lineNumbers(it) }
        val won: Boolean
            get() = squares.any { it.filled() } || columns().any { it.filled() }

        fun mark(draw: Int) {
            squares.forEach { line ->
                line.forEach {
                    if (it.value == draw) {
                        it.marked = true
                    }
                }
            }
        }
        fun score(lastDraw: Int) = lastDraw * squares.flatten().filter { !it.marked }.sumOf { it.value }

        private fun lineNumbers(line: String): List<Square> {
            val match = lineRegex.matchEntire(line) ?: error("Wrong line format: $line")
            return match.groupValues.drop(1).map { Square(it.trim().toInt()) }
        }

        private fun columns(): List<List<Square>> = squares[0].indices.map { i -> squares.map { it[i] } }
        private fun List<Square>.filled() = all { it.marked }
    }

    private class Game(lines: List<String>) {
        val draws: List<Int> = lines[0].split(',').map { it.toInt() }
        val cards: List<Card> = lines.drop(2).filter { it.isNotBlank() }.chunked(5).map { Card(it) }
    }
}
