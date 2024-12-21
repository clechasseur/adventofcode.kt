package io.github.clechasseur.adventofcode2021

import io.github.clechasseur.adventofcode2021.data.Day25Data
import io.github.clechasseur.adventofcode2021.util.Direction
import io.github.clechasseur.adventofcode2021.util.Pt

object Day25 {
    private val data = Day25Data.data

    fun part1(): Int = generateSequence(data.toSeafloor()) {
        val next = it.move()
        if (it == next) null else next
    }.count()

    private data class Seafloor(val facingEast: Set<Pt>, val facingSouth: Set<Pt>, val xSize: Int, val ySize: Int) {
        fun move(): Seafloor {
            val newFacingEast = mutableSetOf<Pt>()
            val newFacingSouth = mutableSetOf<Pt>()
            facingEast.forEach { cucumber ->
                val dest = destination(cucumber, Direction.RIGHT.displacement)
                if (facingEast.contains(dest) || facingSouth.contains(dest)) {
                    newFacingEast.add(cucumber)
                } else {
                    newFacingEast.add(dest)
                }
            }
            facingSouth.forEach { cucumber ->
                val dest = destination(cucumber, Direction.DOWN.displacement)
                if (newFacingEast.contains(dest) || facingSouth.contains(dest)) {
                    newFacingSouth.add(cucumber)
                } else {
                    newFacingSouth.add(dest)
                }
            }
            return Seafloor(newFacingEast, newFacingSouth, xSize, ySize)
        }

        private fun destination(cucumber: Pt, displacement: Pt): Pt {
            val dest = cucumber + displacement
            if (dest.x >= xSize) {
                return Pt(0, dest.y)
            } else if (dest.y >= ySize) {
                return Pt(dest.x, 0)
            }
            return dest
        }
    }

    private fun String.toSeafloor(): Seafloor {
        val facingEast = mutableSetOf<Pt>()
        val facingSouth = mutableSetOf<Pt>()
        lines().forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                if (c == '>') {
                    facingEast.add(Pt(x, y))
                } else if (c == 'v') {
                    facingSouth.add(Pt(x, y))
                }
            }
        }
        return Seafloor(facingEast, facingSouth, lines().first().length, lines().size);
    }
}
