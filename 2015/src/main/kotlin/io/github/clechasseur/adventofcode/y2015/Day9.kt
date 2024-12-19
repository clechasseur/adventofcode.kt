package io.github.clechasseur.adventofcode.y2015

import io.github.clechasseur.adventofcode.util.permutations
import io.github.clechasseur.adventofcode.y2015.data.Day9Data

object Day9 {
    private val input = Day9Data.input

    private val distRegex = """^(\w+) to (\w+) = (\d+)$""".toRegex()

    fun part1(): Int {
        val distMap = input.lines().toDistMap()
        return permutations(distMap.keys.toList()).minOf { path ->
            path.zipWithNext().sumOf { (a, b) -> distMap[a]!![b]!! }
        }
    }

    fun part2(): Int {
        val distMap = input.lines().toDistMap()
        return permutations(distMap.keys.toList()).maxOf { path ->
            path.zipWithNext().sumOf { (a, b) -> distMap[a]!![b]!! }
        }
    }

    private fun List<String>.toDistMap(): Map<String, Map<String, Int>> {
        val distMap = mutableMapOf<String, MutableMap<String, Int>>()
        forEach { line ->
            val match = distRegex.matchEntire(line) ?: error("Wrong dist line: $line")
            val (a, b, dist) = match.destructured
            distMap.addDist(a, b, dist.toInt())
        }
        return distMap
    }

    private fun MutableMap<String, MutableMap<String, Int>>.addDist(a: String, b: String, dist: Int) {
        computeIfAbsent(a) { mutableMapOf() }[b] = dist
        computeIfAbsent(b) { mutableMapOf() }[a] = dist
    }
}
