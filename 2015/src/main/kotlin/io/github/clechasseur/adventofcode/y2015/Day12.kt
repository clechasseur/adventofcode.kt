package io.github.clechasseur.adventofcode.y2015

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import io.github.clechasseur.adventofcode.y2015.data.Day12Data

object Day12 {
    private const val input = Day12Data.input

    fun part1(): Int = """-?\d+""".toRegex().findAll(input).sumOf { it.value.toInt() }

    fun part2(): Int = value(Parser.default().parse(StringBuilder(input)))

    private fun value(v: Any?): Int = when (v) {
        is JsonArray<*> -> arrayValue(v)
        is JsonObject -> objValue(v)
        else -> v?.toString()?.toIntOrNull() ?: 0
    }

    private fun arrayValue(v: JsonArray<*>): Int = v.sumOf { value(it) }

    private fun objValue(v: JsonObject): Int = if (v.values.any { it == "red" }) 0 else v.values.sumOf { value(it) }
}
