package io.github.clechasseur.adventofcode2021

import io.github.clechasseur.adventofcode2021.data.Day13Data
import io.github.clechasseur.adventofcode2021.util.Pt

object Day13 {
    private val data = Day13Data.data
    private val foldData = Day13Data.foldData

    fun part1(): Int = data.toPage().foldLeft(655).dots.size

    fun part2(): String = pageSequence().last().toAscii()

    private data class Page(val dots: Set<Pt>) {
        fun foldLeft(x: Int) = Page(dots.map { dot ->
            if (dot.x < x) dot else Pt(dot.x - (dot.x - x) * 2, dot.y)
        }.toSet())

        fun foldUp(y: Int) = Page(dots.map { dot ->
            if (dot.y < y) dot else Pt(dot.x, dot.y - (dot.y - y) * 2)
        }.toSet())

        fun toAscii(): String {
            val maxX = dots.maxOf { it.x }
            val maxY = dots.maxOf { it.y }
            return (0..maxY).joinToString("\n") { y ->
                (0..maxX).map { x -> if (Pt(x, y) in dots) '#' else '.' }.joinToString("")
            }
        }
    }

    private fun String.toPage(): Page = Page(lines().map { line ->
        val (x, y) = line.split(",").map { it.toInt() }
        Pt(x, y)
    }.toSet())

    private val foldRegex = """fold along ([xy])=(\d+)""".toRegex()

    private fun pageSequence(): Sequence<Page> = sequence {
        var page = data.toPage()
        yield(page)
        for (foldLine in foldData.lines()) {
            val (dir, pos) = (foldRegex.matchEntire(foldLine) ?: error("Wrong fold line: $foldLine")).destructured
            page = if (dir == "x") {
                page.foldLeft(pos.toInt())
            } else {
                page.foldUp(pos.toInt())
            }
            yield(page)
        }
    }
}
