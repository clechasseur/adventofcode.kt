package io.github.clechasseur.adventofcode2021

import io.github.clechasseur.adventofcode2021.data.Day22Data
import io.github.clechasseur.adventofcode2021.util.Pt3D
import kotlin.math.max
import kotlin.math.min

object Day22 {
    private val data = Day22Data.data

    fun part1(): Int = data.lineSequence().take(20).map { it.toCuboid() }.fold(Core(emptySet())) { core, cuboid ->
        core apply cuboid
    }.cubes.size

    fun part2(): Long = countOn(data.lines().map { it.toCuboid() })

    private class Core(val cubes: Set<Pt3D>)

    private class Cuboid(val on: Boolean, val xr: IntRange, val yr: IntRange, val zr: IntRange) {
        val cubes: Set<Pt3D>
            get() = xr.flatMap { x ->
                yr.flatMap { y ->
                    zr.map { z -> Pt3D(x, y, z) }
                }
            }.toSet()

        val size: Long
            get() = xr.size * yr.size * zr.size
    }

    private infix fun Core.apply(cuboid: Cuboid): Core = if (cuboid.on) {
        Core(cubes + cuboid.cubes)
    } else {
        Core(cubes - cuboid.cubes)
    }

    private val IntRange.size: Long
        get() = (last - first + 1).toLong()

    private infix fun Cuboid.overlap(right: Cuboid): Cuboid? {
        val maxx = max(xr.first, right.xr.first)
        val maxy = max(yr.first, right.yr.first)
        val maxz = max(zr.first, right.zr.first)
        val minxp = min(xr.last, right.xr.last)
        val minyp = min(yr.last, right.yr.last)
        val minzp = min(zr.last, right.zr.last)
        return if ((minxp - maxx) >= 0 && (minyp - maxy) >= 0 && (minzp - maxz) >= 0) {
            Cuboid(true, maxx..minxp, maxy..minyp, maxz..minzp)
        } else null
    }

    private fun countOn(cuboids: List<Cuboid>): Long {
        // had help here
        var on = 0L
        val counted = mutableListOf<Cuboid>()
        cuboids.reversed().forEach { cuboid ->
            if (cuboid.on) {
                val dead = mutableListOf<Cuboid>()
                counted.forEach {
                    val overlap = it overlap cuboid
                    if (overlap != null) {
                        dead.add(overlap)
                    }
                }
                on += cuboid.size
                on -= countOn(dead)
            }
            counted.add(cuboid)
        }
        return on
    }

    private val cuboidRegex = """^(on|off) x=(-?\d+)\.\.(-?\d+),y=(-?\d+)\.\.(-?\d+),z=(-?\d+)\.\.(-?\d+)$""".toRegex()

    private fun String.toCuboid(): Cuboid {
        val match = cuboidRegex.matchEntire(this) ?: error("Wrong cuboid: $this")
        val (onOff, xmin, xmax, ymin, ymax, zmin, zmax) = match.destructured
        return Cuboid(
            on=onOff == "on",
            xr=xmin.toInt()..xmax.toInt(),
            yr=ymin.toInt()..ymax.toInt(),
            zr=zmin.toInt()..zmax.toInt(),
        )
    }
}
