package io.github.clechasseur.adventofcode.y2015

import io.github.clechasseur.adventofcode.y2015.data.Day8Data

object Day8 {
    private val input = Day8Data.input

    fun part1(): Int = input.lines().sumOf { it.numCharCodes - it.numCharsInMemory }

    fun part2(): Int = input.lines().sumOf { it.encode().length - it.numCharCodes }

    private val String.numCharCodes: Int
        get() = length

    private val String.numCharsInMemory: Int
        get() = replace("""\"""", "\"")
            .replace("""\\""", """\""")
            .replace("""\\x[0-9a-f]{2}""".toRegex(), "x").length - 2

    private fun String.encode(): String {
        val encoded = replace("""\""", """\\""")
            .replace("\"", """\"""")
        return "\"$encoded\""
    }
}
