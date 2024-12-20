package org.clechasseur.adventofcode2020

data class Pt4D(val x: Int, val y: Int, val z: Int, val w: Int) : Comparable<Pt4D> {
    companion object {
        val ZERO = Pt4D(0, 0, 0, 0)
    }

    override fun toString(): String = "($x, $y, $z, $w)"

    override fun compareTo(other: Pt4D): Int {
        var cmp = x - other.x
        if (cmp == 0) {
            cmp = y - other.y
            if (cmp == 0) {
                cmp = z - other.z
                if (cmp == 0) {
                    cmp = w - other.w
                }
            }
        }
        return cmp
    }

    operator fun plus(pt: Pt4D): Pt4D = Pt4D(x + pt.x, y + pt.y, z + pt.z, w + pt.w)
    operator fun minus(pt: Pt4D): Pt4D = Pt4D(x - pt.x, y - pt.y, z - pt.z, w - pt.w)
}
