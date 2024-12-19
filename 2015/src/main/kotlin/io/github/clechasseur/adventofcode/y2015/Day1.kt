package io.github.clechasseur.adventofcode.y2015

import io.github.clechasseur.adventofcode.y2015.data.Day1Data

object Day1 {
    private const val input = Day1Data.input

    fun part1(): Int = input.mapToFloorMoves().sum()

    fun part2(): Int = input.mapToFloorMoves().foldIndexed(0 to 0) { idx, (cidx, sum), i ->
        (if (sum == -1) cidx else idx + 1) to (if (sum == -1) -1 else sum + i)
    }.first

    private fun String.mapToFloorMoves(): List<Int> = map { when (it) {
        '(' -> 1
        ')' -> -1
        else -> error("Wrong input char: $it")
    } }
}
