package io.github.clechasseur.adventofcode.y2015

import io.github.clechasseur.adventofcode.util.Direction
import io.github.clechasseur.adventofcode.util.Pt
import io.github.clechasseur.adventofcode.y2015.data.Day3Data

object Day3 {
    private const val input = Day3Data.input

    fun part1(): Int = input.map { it.toDirection() }.fold(Pt.ZERO to setOf(Pt.ZERO)) { (pos, s), d ->
        val newPos = pos + d.displacement
        newPos to s + newPos
    }.second.size

    fun part2(): Int = input.map {
        it.toDirection()
    }.foldIndexed((Pt.ZERO to Pt.ZERO) to setOf(Pt.ZERO)) { idx, (poss, s), d ->
        val (santaPos, roboPos) = poss
        if (idx % 2 == 0) {
            val newPos = santaPos + d.displacement
            (newPos to roboPos) to s + newPos
        } else {
            val newPos = roboPos + d.displacement
            (santaPos to newPos) to s + newPos
        }
    }.second.size

    private fun Char.toDirection(): Direction = when (this) {
        '^' -> Direction.UP
        '<' -> Direction.LEFT
        'v' -> Direction.DOWN
        '>' -> Direction.RIGHT
        else -> error("Wrong direction char: $this")
    }
}
