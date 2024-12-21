package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.util.Pt
import io.github.clechasseur.adventofcode.y2022.data.Day14Data
import kotlin.math.sign

object Day14 {
    private val input = Day14Data.input

    fun part1(): Int = generateSequence(input.toCave(hasFloor = false)) {
        it.dropOneSand(Pt(500, 0))
    }.last().sand.size

    fun part2(): Int = generateSequence(input.toCave(hasFloor = true)) {
        it.dropOneSand(Pt(500, 0))
    }.last().sand.size

    private data class Cave(val solid: Set<Pt>, val sand: Set<Pt>, val hasFloor: Boolean) {
        val bottom: Int = solid.maxOf { it.y } + 2

        fun occupied(pt: Pt): Boolean = solid.contains(pt) || sand.contains(pt) || (hasFloor && pt.y >= bottom)

        fun dropOneSand(from: Pt): Cave? {
            if (occupied(from)) {
                return null
            }
            var pt = from
            while (pt.y < bottom) {
                var nextPt = pt + Pt(0, 1)
                if (occupied(nextPt)) {
                    nextPt = pt + Pt(-1, 1)
                    if (occupied(nextPt)) {
                        nextPt = pt + Pt(1, 1)
                        if (occupied(nextPt)) {
                            return Cave(solid, sand + pt, hasFloor)
                        }
                    }
                }
                pt = nextPt
            }
            return null
        }
    }

    private fun String.toCave(hasFloor: Boolean): Cave = Cave(lines().flatMap { line ->
        line.split(" -> ").map { pt ->
            val (x, y) = pt.split(",")
            Pt(x.toInt(), y.toInt())
        }.zipWithNext().flatMap { it.asLine() }
    }.toSet(), emptySet(), hasFloor)

    private fun Pair<Pt, Pt>.asLine(): Sequence<Pt> {
        val displacement = Pt((second.x - first.x).sign, (second.y - first.y).sign)
        return generateSequence(first) { it + displacement }.takeWhile { (it - displacement) != second }
    }
}
