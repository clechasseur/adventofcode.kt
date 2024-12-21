package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.util.Direction
import io.github.clechasseur.adventofcode.util.Pt
import io.github.clechasseur.adventofcode.util.move
import io.github.clechasseur.adventofcode.util.toDirection
import io.github.clechasseur.adventofcode.y2022.data.Day9Data
import kotlin.math.abs
import kotlin.math.sign

object Day9 {
    private val input = Day9Data.input

    fun part1(): Int = runSimulation(2)

    fun part2(): Int = runSimulation(10)

    private fun runSimulation(ropeSize: Int): Int {
        var rope = Rope(ropeSize)
        val tailVisited = mutableSetOf(rope.tail)
        input.toMoveSequence().forEach { move ->
            rope = rope.move(move)
            tailVisited.add(rope.tail)
        }
        return tailVisited.size
    }

    private data class Rope(val knots: List<Pt>) {
        constructor(size: Int) : this(List(size) { Pt.ZERO })

        val head: Pt
            get() = knots.first()
        val tail: Pt
            get() = knots.last()

        fun move(dir: Direction): Rope {
            val newKnots = mutableListOf(head.move(dir))
            for (knot in knots.drop(1)) {
                val prevKnot = newKnots.last()
                var newKnot = knot
                if (abs(newKnot.x - prevKnot.x) > 1 || abs(newKnot.y - prevKnot.y) > 1) {
                    newKnot = Pt(
                        newKnot.x + (prevKnot.x - newKnot.x).sign,
                        newKnot.y + (prevKnot.y - newKnot.y).sign
                    )
                }
                newKnots.add(newKnot)
            }
            return Rope(newKnots)
        }
    }

    private fun String.toMoveSequence(): Sequence<Direction> = lineSequence().flatMap { line ->
        val (dir, distance) = line.split(' ')
        val move = dir.single().toDirection()
        generateSequence { move }.take(distance.toInt())
    }
}
