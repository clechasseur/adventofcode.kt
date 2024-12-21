package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.util.Pt3D
import io.github.clechasseur.adventofcode.y2022.data.Day18Data

object Day18 {
    private val input = Day18Data.input

    private val displacements = listOf(
        Pt3D(-1, 0, 0),
        Pt3D(1, 0, 0),
        Pt3D(0, -1, 0),
        Pt3D(0, 1, 0),
        Pt3D(0, 0, -1),
        Pt3D(0, 0, 1)
    )

    fun part1(): Int = Volume(cubes = input.lines().map { it.toPt3D() }.toSet()).exposedSides

    fun part2(): Int {
        val volume = Volume(cubes = input.lines().map { it.toPt3D() }.toSet())
        return volume.exposedSides - volume.airExposedSides
    }

    private class Volume(val cubes: Set<Pt3D>) {
        val exposedSides: Int
            get() = cubes.sumOf { exposedSidesFor(it) }

        val airExposedSides: Int
            get() {
                val pts = cubes.flatMap { pt ->
                    displacements.map { pt + it }
                }.filter { it !in cubes }.toSet()
                val expanded = mutableSetOf<Pt3D>()
                pts.forEach { pt ->
                    if (pt !in expanded) {
                        val expandedPt = expandAirPocket(pt)
                        if (expandedPt != null) {
                            expanded.addAll(expandedPt)
                        }
                    }
                }
                return Volume(cubes = expanded).exposedSides
            }

        private fun exposedSidesFor(pt: Pt3D): Int = displacements.map { pt + it }.count { adjacentPt ->
            adjacentPt !in cubes
        }

        private fun inVoid(pt: Pt3D): Boolean = pt.x < (cubes.minOf { it.x } - 1) ||
                pt.x > (cubes.maxOf { it.x } + 1) ||
                pt.y < (cubes.minOf { it.y } - 1) ||
                pt.y > (cubes.maxOf { it.y } + 1) ||
                pt.z < (cubes.minOf { it.z } - 1) ||
                pt.z > (cubes.maxOf { it.z } + 1)

        private fun expandAirPocket(pt: Pt3D): Set<Pt3D>? {
            val expanded = mutableSetOf(pt)
            var edges = setOf(pt)
            while (edges.isNotEmpty()) {
                edges = edges.flatMap { edge ->
                    displacements.map { edge + it }
                }.filter { edge ->
                    edge !in expanded && edge !in cubes
                }.toSet()
                if (edges.any { inVoid(it) }) {
                    return null
                }
                expanded.addAll(edges)
            }
            return expanded
        }
    }

    private fun String.toPt3D(): Pt3D {
        val (x, y, z) = split(',')
        return Pt3D(x.toInt(), y.toInt(), z.toInt())
    }
}
