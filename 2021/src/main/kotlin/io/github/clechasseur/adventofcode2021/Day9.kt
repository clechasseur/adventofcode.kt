package io.github.clechasseur.adventofcode2021

import io.github.clechasseur.adventofcode2021.data.Day9Data
import io.github.clechasseur.adventofcode2021.dij.Dijkstra
import io.github.clechasseur.adventofcode2021.dij.Graph
import io.github.clechasseur.adventofcode2021.util.Direction
import io.github.clechasseur.adventofcode2021.util.Pt

object Day9 {
    private val data = Day9Data.data

    private val heightmap = data.lines().map { line -> line.toList().map { it.toString().toInt() } }

    fun part1(): Int = lowpoints().sumOf { it.second + 1 }

    fun part2(): Int = lowpoints().map { (lowpoint, _) ->
        Dijkstra.build(PointsGraph(), lowpoint).dist.filterValues { it != Long.MAX_VALUE }.size
    }.sortedDescending().take(3).fold(1) { acc, i -> acc * i }

    private fun lowpoints(): List<Pair<Pt, Int>> {
        val points = mutableListOf<Pair<Pt, Int>>()
        for (y in heightmap.indices) {
            for (x in heightmap[y].indices) {
                if (Direction.displacements.mapNotNull { adjacent(Pt(x, y), it) }.all { it > heightmap[y][x] }) {
                    points.add(Pt(x, y) to heightmap[y][x])
                }
            }
        }
        return points
    }

    private fun adjacent(pt: Pt, move: Pt): Int? {
        val target = pt + move
        if (target.y in heightmap.indices && target.x in heightmap[target.y].indices) {
            return heightmap[target.y][target.x]
        }
        return null
    }

    private class PointsGraph : Graph<Pt> {
        override fun allPassable(): List<Pt> = heightmap.indices.flatMap { y ->
            heightmap[y].indices.map { x -> Pt(x, y) }
        }.filter { heightmap[it.y][it.x] != 9 }

        override fun neighbours(node: Pt): List<Pt> {
            val value = heightmap[node.y][node.x]
            val higher = mutableListOf<Pt>()
            Direction.displacements.forEach { move ->
                val nextValue = adjacent(node, move)
                if (nextValue != null && nextValue > value) {
                    higher.add(node + move)
                }
            }
            return higher.filter { heightmap[it.y][it.x] != 9 }
        }

        override fun dist(a: Pt, b: Pt): Long = 1L
    }
}
