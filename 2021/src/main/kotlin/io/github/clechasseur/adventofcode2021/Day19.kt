package io.github.clechasseur.adventofcode2021

import io.github.clechasseur.adventofcode2021.data.Day19Data
import io.github.clechasseur.adventofcode2021.util.Pt3D
import io.github.clechasseur.adventofcode2021.util.manhattan

object Day19 {
    private val data = Day19Data.data

    fun part1(): Int = data.toScanners().merge().beacons.size

    fun part2(): Int {
        val map = data.toScanners().merge()
        return map.pos.maxOf { s1 ->
            map.pos.filter { it != s1 }.maxOf { s2 -> manhattan(s1, s2) }
        }
    }

    private val facings: List<(Pt3D) -> Pt3D> = listOf(
        { it },
        { Pt3D(-it.y, it.x, it.z) },
        { Pt3D(-it.x, -it.y, it.z) },
        { Pt3D(it.y, -it.x, it.z) },
    )

    private val rotations: List<(Pt3D) -> Pt3D> = listOf(
        { it },
        { Pt3D(it.x, -it.z, it.y) },
        { Pt3D(it.x, -it.y, -it.z) },
        { Pt3D(it.x, it.z, -it.y) },
        { Pt3D(it.z, it.y, -it.x) },
        { Pt3D(-it.z, it.y, it.x) },
    )

    private val orientations: List<(Pt3D) -> Pt3D> = facings.flatMap { facing ->
        rotations.map { rotation -> { rotation(facing(it)) } }
    }

    private class DistanceInfo(val dist: Pt3D, val from: Pt3D, val to: Pt3D) : Comparable<DistanceInfo> {
        override fun equals(other: Any?): Boolean = other != null && other is DistanceInfo && other.dist == dist
        override fun hashCode(): Int = dist.hashCode()
        override fun compareTo(other: DistanceInfo): Int = dist.compareTo(other.dist)
    }

    private class Scanner(val beacons: Set<Pt3D>, val pos: Set<Pt3D>) {
        fun allOrientations(): Sequence<Scanner> = orientations.asSequence().map { orientation ->
            Scanner(beacons.map { orientation(it) }.toSet(), pos)
        }

        fun distanceMap(): Set<DistanceInfo> = beacons.flatMap { b1 ->
            beacons.filter { it != b1 }.map { b2 -> DistanceInfo(b1 - b2, b1, b2) }
        }.toSet()

        fun connect(other: Scanner): Scanner? {
            val leftDm = distanceMap()
            val (rightDm, right) = other.allOrientations().map { it.distanceMap() to it }.find { (rdm, _) ->
                leftDm.intersect(rdm).size >= 66
            } ?: return null
            leftDm.forEach { leftInfo ->
                rightDm.forEach { rightInfo ->
                    if (leftInfo.dist == rightInfo.dist) {
                        val rightPos = leftInfo.from - rightInfo.from
                        return Scanner(beacons + right.beacons.map { it + rightPos }.toSet(), pos + rightPos)
                    }
                }
            }
            return null
        }
    }

    private fun List<Scanner>.merge(): Scanner {
        val scanners = toMutableList()
        loop@while (scanners.size > 1) {
            for (i in scanners.indices.drop(1)) {
                val connected = scanners.first().connect(scanners[i])
                if (connected != null) {
                    scanners.removeAt(i)
                    scanners[0] = connected
                    continue@loop
                }
            }
            error("Should not reach this point")
        }
        return scanners.first()
    }

    private val beaconRegex = """^(-?\d+),(-?\d+),(-?\d+)$""".toRegex()

    private fun String.toScanners(): List<Scanner> {
        val it = lines().iterator()
        val scanners = mutableListOf<Scanner>()
        var pos = setOf(Pt3D.ZERO)
        while (it.hasNext()) {
            it.next()
            val beacons = mutableListOf<Pt3D>()
            while (it.hasNext()) {
                val beaconLine = it.next()
                if (beaconLine.isNotBlank()) {
                    val beaconMatch = beaconRegex.matchEntire(beaconLine) ?: error("Wrong beacon line: $beaconLine")
                    val (x, y, z) = beaconMatch.destructured
                    beacons.add(Pt3D(x.toInt(), y.toInt(), z.toInt()))
                } else {
                    break
                }
            }
            scanners.add(Scanner(beacons.toSet(), pos))
            pos = emptySet()
        }
        return scanners
    }
}
