package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.y2022.data.Day6Data

object Day6 {
    private const val input = Day6Data.input

    fun part1(): Int = input.windowedSequence(4).withIndex().first { (_, s) -> s.allDifferent }.index + 4

    fun part2(): Int = input.windowedSequence(14).withIndex().first { (_, s) -> s.allDifferent }.index + 14

    private val String.allDifferent: Boolean
        get() = toList().distinct().size == length
}
