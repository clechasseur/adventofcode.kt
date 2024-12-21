package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.y2022.data.Day10Data

object Day10 {
    private val input = Day10Data.input

    private val addxRegex = """^addx (-?\d+)$""".toRegex()

    fun part1(): Long {
        val xValues = input.xRegisterValues().toList()
        return xValues[19] * 20L + xValues[59] * 60L + xValues[99] * 100L +
                xValues[139] * 140L + xValues[179] * 180L + xValues[219] * 220L
    }

    fun part2(): String = input.xRegisterValues().withIndex().chunked(40).take(6).joinToString("\n") { lineValues ->
        lineValues.joinToString("") { (cycle, x) ->
            val sprite = (x - 1)..(x + 1)
            if (cycle % 40 in sprite) "#" else "."
        }
    }

    private fun String.xRegisterValues(): Sequence<Long> = sequence {
        var x = 1L
        yield(x)
        lineSequence().forEach { line -> when (line) {
            "noop" -> yield(x)
            else -> {
                val match = addxRegex.matchEntire(line) ?: error("Wrong addx: $line")
                yield(x)
                x += match.groupValues[1].toLong()
                yield(x)
            }
        } }
    }
}
