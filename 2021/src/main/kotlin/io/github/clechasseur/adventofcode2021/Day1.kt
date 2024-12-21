package io.github.clechasseur.adventofcode2021

import io.github.clechasseur.adventofcode2021.data.Day1Data

object Day1 {
    private val input = Day1Data.input

    fun part1(): Int = input.zipWithNext().count { it.first < it.second }

    fun part2(): Int = input.zipWithNext().zipWithNext { p1, p2 ->
        SlidingWindow(p1.first, p1.second, p2.second)
    }.zipWithNext().count { it.first < it.second }

    private data class SlidingWindow(val x: Int, val y: Int, val z: Int) : Comparable<SlidingWindow> {
        override fun compareTo(other: SlidingWindow): Int = (x + y + z) - (other.x + other.y + other.z)
    }
}
