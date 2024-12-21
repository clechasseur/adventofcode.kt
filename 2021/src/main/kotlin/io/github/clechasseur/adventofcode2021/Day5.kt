package io.github.clechasseur.adventofcode2021

import io.github.clechasseur.adventofcode2021.data.Day5Data
import io.github.clechasseur.adventofcode2021.util.Pt

object Day5 {
    private val data = Day5Data.data

    private val linesRegex = """^(\d+),(\d+) -> (\d+),(\d+)$""".toRegex()

    private val lines: List<Pair<Pt, Pt>> = data.lines().map {
        val match = linesRegex.matchEntire(it) ?: error("Wrong line format: $it")
        val (x1, y1, x2, y2) = match.destructured
        Pt(x1.toInt(), y1.toInt()) to Pt(x2.toInt(), y2.toInt())
    }

    fun part1(): Int = hotPoints(false)

    fun part2(): Int = hotPoints(true)

    private fun hotPoints(diag: Boolean): Int {
        val points = mutableMapOf<Pt, Int>()
        lines.forEach { line ->
            linePoints(line, diag).forEach { pt ->
                points[pt] = points.getOrDefault(pt, 0) + 1
            }
        }
        return points.count { it.value > 1 }
    }

    private fun linePoints(line: Pair<Pt, Pt>, diag: Boolean): Sequence<Pt> = if (line.first.y == line.second.y) {
        if (line.first.x <= line.second.x) {
            generateSequence(line.first) { if (it.x < line.second.x) Pt(it.x + 1, it.y) else null }
        } else {
            linePoints(line.second to line.first, diag)
        }
    } else if (line.first.x == line.second.x) {
        linePoints(Pt(line.first.y, line.first.x) to Pt(line.second.y, line.second.x), diag).map {
            Pt(it.y, it.x)
        }
    } else if (!diag) {
        emptySequence()
    } else if (line.first.x < line.second.x) {
        if (line.first.y < line.second.y) {
            linePoints(Pt(line.first.x, line.first.y) to Pt(line.second.x, line.first.y), false).mapIndexed { i, pt ->
                Pt(pt.x, pt.y + i)
            }
        } else {
            linePoints(Pt(line.first.x, line.second.y) to Pt(line.second.x, line.first.y), true).mapIndexed { i, pt ->
                Pt(pt.x, line.first.y - i)
            }
        }
    } else {
        linePoints(line.second to line.first, true)
    }
}
