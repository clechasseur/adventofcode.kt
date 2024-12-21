package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.y2022.data.Day5Data

object Day5 {
    private val initial = listOf(
        "<REDACTED>",
    )
    private val moves = Day5Data.moves

    private val moveRegex = """^move (\d+) from (\d+) to (\d+)$""".toRegex()

    fun part1(): String {
        val moveList = moves.lines().map { it.toMove() }
        val final = moveList.fold(initial) { stacks, move -> stacks.perform(move, false) }
        return final.joinToString("") { it.last().toString() }
    }

    fun part2(): String {
        val moveList = moves.lines().map { it.toMove() }
        val final = moveList.fold(initial) { stacks, move -> stacks.perform(move, true) }
        return final.joinToString("") { it.last().toString() }
    }

    private data class Move(val num: Int, val from: Int, val to: Int)

    private fun String.toMove(): Move {
        val match = moveRegex.matchEntire(this) ?: error("Wrong move: $this")
        val (num, from, to) = match.destructured
        return Move(num.toInt(), from.toInt() - 1, to.toInt() - 1)
    }

    private fun List<String>.perform(move: Move, crateMover9001: Boolean): List<String> {
        val mod = toMutableList()
        var toMove = mod[move.from].substring(mod[move.from].length - move.num)
        if (!crateMover9001) {
            toMove = toMove.reversed()
        }
        mod[move.to] = mod[move.to] + toMove
        mod[move.from] = mod[move.from].dropLast(move.num)
        return mod.toList()
    }
}
