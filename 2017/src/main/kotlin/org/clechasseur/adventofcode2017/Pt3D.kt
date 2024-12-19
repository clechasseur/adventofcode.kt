package org.clechasseur.adventofcode2017

import kotlin.math.abs

data class Pt3D(val x: Int, val y: Int, val z: Int) : Comparable<Pt3D> {
    companion object {
        internal val strRegex = Regex("""^<x=(-?\d+), y=(-?\d+), z=(-?\d+)>$""")

        val ZERO = Pt3D(0, 0, 0)
    }

    override fun toString(): String = "($x, $y, $z)"

    override fun compareTo(other: Pt3D): Int = manhattan(this, ZERO) - manhattan(other, ZERO)

    operator fun plus(pt: Pt3D) = Pt3D(x + pt.x, y + pt.y, z + pt.z)
    operator fun minus(pt: Pt3D) = Pt3D(x - pt.x, y - pt.y, z - pt.z)
}

fun manhattan(a: Pt3D, b: Pt3D) = abs(a.x - b.x) + abs(a.y - b.y) + abs(a.z - b.z)

fun String.toPt3D(): Pt3D {
    val match = Pt3D.strRegex.matchEntire(this)
    requireNotNull(match) { "Invalid string representation of Pt3D" }
    val (x, y, z) = match.groupValues.drop(1).map { it.toInt() }
    return Pt3D(x, y, z)
}
