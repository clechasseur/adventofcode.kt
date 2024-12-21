package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.util.Pt
import io.github.clechasseur.adventofcode.util.manhattan
import kotlin.math.sign

object Day15 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val inputRegex = """^Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)$""".toRegex()

    fun part1(): Int {
        val arrangement = input.toArrangement()
        val minSensor = arrangement.sensors.keys.minBy { it.x }
        val maxSensor = arrangement.sensors.keys.maxBy { it.x }
        val minX = minSensor.x - manhattan(minSensor, arrangement.sensors[minSensor]!!)
        val maxX = maxSensor.x + manhattan(maxSensor, arrangement.sensors[maxSensor]!!)
        return (minX..maxX).map { Pt(it, 2_000_000) }.count { pt ->
            arrangement.sensors.entries.any { (sensor, beacon) ->
                manhattan(sensor, pt) <= manhattan(sensor, beacon)
            } && pt !in arrangement.beacons
        }
    }

    fun part2(): Long {
        val arrangement = input.toArrangement()
        val distressBeacon = arrangement.outsideEdges().filter {
            it.x in 0..4_000_000 && it.y in 0..4_000_000
        }.first {
            arrangement.notInRange(it)
        }
        return distressBeacon.x.toLong() * 4_000_000L + distressBeacon.y.toLong()
    }

    private data class Arrangement(val sensors: Map<Pt, Pt>) {
        val beacons: Collection<Pt>
            get() = sensors.values
        val ranges: Map<Pt, Int> = sensors.mapValues { (sensor, beacon) ->
            manhattan(sensor, beacon)
        }

        fun notInRange(pt: Pt): Boolean = ranges.all { (sensor, range) ->
            manhattan(sensor, pt) > range
        }

        fun outsideEdges(): Sequence<Pt> = ranges.asSequence().flatMap { (sensor, range) ->
            val a = sensor + Pt(-(range + 1), 0)
            val b = sensor + Pt(0, -(range + 1))
            val c = sensor + Pt(range + 1, 0)
            val d = sensor + Pt(0, range + 1)
            diagonal(a, b) + diagonal(b, c) + diagonal(c, d) + diagonal(d, a)
        }

        private fun diagonal(a: Pt, b: Pt): Sequence<Pt> {
            val displacement = Pt((b.x - a.x).sign, (b.y - a.y).sign)
            return generateSequence(a) { it + displacement }.takeWhile { it - displacement != b }
        }
    }

    private fun String.toArrangement(): Arrangement = Arrangement(lines().associate { line ->
        val match = inputRegex.matchEntire(line) ?: error("Invalid sensor declaration: $line")
        val (sensorX, sensorY, beaconX, beaconY) = match.destructured
        Pt(sensorX.toInt(), sensorY.toInt()) to Pt(beaconX.toInt(), beaconY.toInt())
    })
}
