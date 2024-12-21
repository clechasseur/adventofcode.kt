package io.github.clechasseur.adventofcode2021.util

import kotlin.math.abs

data class Pt(val x: Int, val y: Int) : Comparable<Pt> {
    companion object {
        val ZERO = Pt(0, 0)
    }

    override fun toString(): String = "($x, $y)"

    override fun compareTo(other: Pt): Int {
        var cmp = x - other.x
        if (cmp == 0) {
            cmp = y - other.y
        }
        return cmp
    }

    operator fun plus(pt: Pt) = Pt(x + pt.x, y + pt.y)
    operator fun minus(pt: Pt) = Pt(x - pt.x, y - pt.y)

    operator fun times(value: Int) = Pt(x * value, y * value)
}

fun manhattan(a: Pt, b: Pt): Int = abs(a.x - b.x) + abs(a.y - b.y)
