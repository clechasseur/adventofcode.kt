package io.github.clechasseur.adventofcode2021

import io.github.clechasseur.adventofcode2021.data.Day15Data
import io.github.clechasseur.adventofcode2021.dij.Dijkstra
import io.github.clechasseur.adventofcode2021.dij.Graph
import io.github.clechasseur.adventofcode2021.util.Direction
import io.github.clechasseur.adventofcode2021.util.Pt

object Day15 {
    private val data = Day15Data.data

    private val riskLevels = data.lines().map { line ->
        line.map { it.toString().toInt() }
    }

    fun part1(): Int {
        val out = Dijkstra.build(RiskGraph(riskLevels), Pt.ZERO)
        return out.dist[Pt(riskLevels[0].indices.last, riskLevels.indices.last)]!!.toInt()
    }

    fun part2(): Int {
        val tiles = (0..4).map { yDisp ->
            (0..4).map { xDisp ->
                displacedRiskLevels(xDisp + yDisp)
            }
        }
        val graph = (0 until (riskLevels.indices.last + 1) * 5).map { y ->
            (0 until (riskLevels[0].indices.last + 1) * 5).map { x ->
                val tile = tiles[y / (riskLevels.indices.last + 1)][x / (riskLevels[0].indices.last + 1)]
                tile[y % (riskLevels.indices.last + 1)][x % (riskLevels[0].indices.last + 1)]
            }
        }
        val out = Dijkstra.build(RiskGraph(graph), Pt.ZERO)
        return out.dist[Pt(graph[0].indices.last, graph.indices.last)]!!.toInt()
    }

    private class RiskGraph(val graph: List<List<Int>>) : Graph<Pt> {
        override fun allPassable(): List<Pt> = graph.indices.flatMap { y ->
            graph[y].indices.map { x -> Pt(x, y) }
        }

        override fun neighbours(node: Pt): List<Pt> = Direction.displacements.mapNotNull { move ->
            val pt = node + move
            if (pt.y in graph.indices && pt.x in graph[pt.y].indices) pt else null
        }

        override fun dist(a: Pt, b: Pt): Long = graph[b.y][b.x].toLong()
    }

    private fun displacedRiskLevels(disp: Int): List<List<Int>> = riskLevels.map { yList ->
        yList.map { it.displacedRisk(disp) }
    }

    private fun Int.displacedRisk(disp: Int) = if (this + disp <= 9) this + disp else this + disp - 9
}
