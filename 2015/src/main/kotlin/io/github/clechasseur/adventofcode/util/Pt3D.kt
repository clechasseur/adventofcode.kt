package io.github.clechasseur.adventofcode.util

import kotlin.math.abs

data class Pt3D(val x: Int, val y: Int, val z: Int) : Comparable<Pt3D> {
    companion object {
        val ZERO = Pt3D(0, 0, 0)
    }

    override fun toString(): String = "($x, $y, $z)"

    override fun compareTo(other: Pt3D): Int {
        var cmp = x - other.x
        if (cmp == 0) {
            cmp = y - other.y
            if (cmp == 0) {
                cmp = z - other.z
            }
        }
        return cmp
    }

    operator fun plus(pt: Pt3D) = Pt3D(x + pt.x, y + pt.y, z + pt.z)
    operator fun minus(pt: Pt3D) = Pt3D(x - pt.x, y - pt.y, z - pt.z)
}

fun manhattan(a: Pt3D, b: Pt3D) = abs(a.x - b.x) + abs(a.y - b.y) + abs(a.z - b.z)
