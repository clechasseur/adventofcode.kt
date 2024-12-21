package io.github.clechasseur.adventofcode2021

import io.github.clechasseur.adventofcode2021.data.Day2Data
import io.github.clechasseur.adventofcode2021.util.Direction
import io.github.clechasseur.adventofcode2021.util.Pt

object Day2 {
    private val data = Day2Data.data

    private val moveRegex = """^(\w+)\s+(\d+)$""".toRegex()

    fun part1(): Int {
        var pt = Pt.ZERO
        data.lines().forEach { line ->
            val (dir, q) = (moveRegex.matchEntire(line) ?: error("Wrong line format: $line")).destructured
            pt += when (dir) {
                "forward" -> Direction.RIGHT
                "up" -> Direction.UP
                "down" -> Direction.DOWN
                else -> error("Wrong direction: $dir")
            }.displacement * q.toInt()
        }
        return pt.x * pt.y
    }

    fun part2(): Int {
        var pt = Pt.ZERO
        var aim = Pt.ZERO
        data.lines().forEach { line ->
            val (dir, q) = (moveRegex.matchEntire(line) ?: error("Wrong line format: $line")).destructured
            when (dir) {
                "forward" -> {
                    pt += Direction.RIGHT.displacement * q.toInt()
                    pt += aim * q.toInt()
                }
                "up" -> aim += Direction.UP.displacement * q.toInt()
                "down" -> aim += Direction.DOWN.displacement * q.toInt()
                else -> error("Wrong direction: $dir")
            }
        }
        return pt.x * pt.y
    }
}
