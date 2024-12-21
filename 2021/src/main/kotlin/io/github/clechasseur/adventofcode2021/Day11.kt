package io.github.clechasseur.adventofcode2021

import io.github.clechasseur.adventofcode2021.util.Pt

object Day11 {
    private val data = """
        <REDACTED>
    """.trimIndent().lines().map { line -> line.toList().map { it.toString().toInt() } }

    fun part1(): Int = steps().map { matrix -> matrix.flatten().count { it == 0 } }.take(100).sum()

    fun part2(): Int = steps().takeWhile { matrix -> matrix.flatten().any { it > 0 } }.count() + 1

    private fun steps(): Sequence<List<List<Int>>> = generateSequence (data) { prev ->
        val next = prev.map { it.toMutableList() }.toMutableList()
        val toIncrement = prev.indices.flatMap { y ->
            prev[y].indices.map { x -> Pt(x, y) to 1 }
        }.toMap().toMutableMap()
        val flashed = mutableSetOf<Pt>()
        while (toIncrement.isNotEmpty()) {
            val thisTime = toIncrement.toMap()
            toIncrement.clear()
            thisTime.forEach { (pt, inc) ->
                next[pt.y][pt.x] = next[pt.y][pt.x] + inc
                if (next[pt.y][pt.x] > 9) {
                    flashed.add(pt)
                    neighbours(next, pt).forEach {
                        toIncrement[it] = toIncrement.getOrDefault(it, 0) + 1
                    }
                }
            }
            flashed.forEach { toIncrement.remove(it) }
        }
        flashed.forEach { pt -> next[pt.y][pt.x] = 0 }
        next.map { it.toList() }.toList()
    }.drop(1)

    private fun <T> neighbours(matrix: List<List<T>>, pt: Pt): List<Pt> = (-1..1).flatMap { y ->
        (-1..1).mapNotNull { x ->
            val new = pt + Pt(x, y)
            if ((x != 0 || y != 0) && new.y in matrix.indices && new.x in matrix[new.y].indices) new else null
        }
    }
}
