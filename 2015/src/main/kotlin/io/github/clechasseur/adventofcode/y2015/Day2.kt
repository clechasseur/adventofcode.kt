package io.github.clechasseur.adventofcode.y2015

import io.github.clechasseur.adventofcode.y2015.data.Day2Data

object Day2 {
    private val input = Day2Data.input

    fun part1(): Int = input.lines().map { it.toPresent() }.sumOf { it.requiredPaper }

    fun part2(): Int = input.lines().map { it.toPresent() }.sumOf { it.requiredRibbon }

    private data class Present(val l: Int, val w: Int, val h: Int) {
        val sides: List<Int>
            get() = listOf(l * w, l * h, w * h)

        val smallestSide: Int
            get() = sides.min()

        val requiredPaper: Int
            get() = sides.sum() * 2 + smallestSide

        val perimeters: List<Int>
            get() = listOf(l * 2 + w * 2, l * 2 + h * 2, w * 2 + h * 2)

        val smallestPerimeter: Int
            get() = perimeters.min()

        val volume: Int
            get() = l * w * h

        val requiredRibbon: Int
            get() = smallestPerimeter + volume
    }

    private fun String.toPresent(): Present {
        val (l, w, h) = split('x')
        return Present(l.toInt(), w.toInt(), h.toInt())
    }
}
