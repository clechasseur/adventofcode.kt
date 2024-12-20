package org.clechasseur.adventofcode2019

import kotlin.math.abs

data class Pt(val x: Int, val y: Int) : Comparable<Pt> {
    companion object {
        val ZERO = Pt(0, 0)
    }

    override fun toString(): String = "($x, $y)"

    override fun compareTo(other: Pt): Int = manhattan(this, ZERO) - manhattan(other, ZERO)

    operator fun plus(pt: Pt) = Pt(x + pt.x, y + pt.y)
    operator fun minus(pt: Pt) = Pt(x - pt.x, y - pt.y)
}

fun manhattan(a: Pt, b: Pt): Int = abs(a.x - b.x) + abs(a.y - b.y)
