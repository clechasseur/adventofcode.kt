package io.github.clechasseur.adventofcode.y2015

import io.github.clechasseur.adventofcode.util.Pt
import io.github.clechasseur.adventofcode.y2015.data.Day18Data

object Day18 {
    private val input = Day18Data.input

    fun part1(): Int = generateSequence(input.toGrid(false)) { it.animate() }.drop(100).first().litCount

    fun part2(): Int = generateSequence(input.toGrid(true)) { it.animate() }.drop(100).first().litCount

    private class Grid(val on: Set<Pt>, val stuck: Set<Pt>) {
        companion object {
            private val corners = setOf(Pt(0, 0), Pt(0, 99), Pt(99, 0), Pt(99, 99))
        }

        constructor(on: Set<Pt>, faulty: Boolean) : this(on, if (faulty) corners else emptySet())

        val litCount: Int
            get() = (on + stuck).size

        fun animate(): Grid = Grid((0..99).flatMap { x ->
            (0..99).map { y ->
                val pt = Pt(x, y)
                pt to animate(pt)
            }
        }.filter { it.second }.map { it.first }.toSet(), stuck)

        private fun state(pt: Pt): Boolean = on.contains(pt) || stuck.contains(pt)

        private fun neighbours(pt: Pt): List<Pt> = (-1..1).flatMap { x ->
            (-1..1).map { y ->
                pt + Pt(x, y)
            }
        } - pt

        private fun litNeighbours(pt: Pt): Int = neighbours(pt).count { state(it) }

        private fun animate(pt: Pt): Boolean = when (state(pt)) {
            true -> litNeighbours(pt) in 2..3
            false -> litNeighbours(pt) == 3
        }
    }

    private fun String.toGrid(faulty: Boolean): Grid = Grid(lines().flatMapIndexed { y, line ->
        line.mapIndexed { x, light -> if (light == '#') Pt(x, y) else null }
    }.filterNotNull().toSet(), faulty)
}
