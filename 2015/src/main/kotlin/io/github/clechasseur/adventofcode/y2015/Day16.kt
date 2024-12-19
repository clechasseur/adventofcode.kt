package io.github.clechasseur.adventofcode.y2015

import io.github.clechasseur.adventofcode.y2015.data.Day16Data

object Day16 {
    private val target = Day16Data.target
    private val input = Day16Data.input

    private val targetRegex = """^(\w+): (\d+)$""".toRegex()
    private val inputRegex = """^Sue (\d+): ((?:(?:, )?\w+: \d+)+)$""".toRegex()

    fun part1(): Int {
        val ourTarget = target.toTarget()
        return input.toAunts().filter { (_, spec) -> spec.matches1(ourTarget) }.asSequence().single().key
    }

    fun part2(): Int {
        val ourTarget = target.toTarget()
        return input.toAunts().filter { (_, spec) -> spec.matches2(ourTarget) }.asSequence().single().key
    }

    private fun Map<String, Int>.matches1(target: Map<String, Int>): Boolean = all { (name, num) ->
        target[name]!! == num
    }

    private fun Map<String, Int>.matches2(target: Map<String, Int>): Boolean = all { (name, num) ->
        matches2(name, num, target[name]!!)
    }

    private fun matches2(name: String, num: Int, target: Int): Boolean = when (name) {
        "cats", "trees" -> num > target
        "pomeranians", "goldfish" -> num < target
        else -> num == target
    }

    private fun String.toTarget(): Map<String, Int> = lines().associate {
        val match = targetRegex.matchEntire(it) ?: error("Wrong target line: $it")
        val (name, num) = match.destructured
        name to num.toInt()
    }

    private fun String.toAunts(): Map<Int, Map<String, Int>> = lines().associate { line ->
        val match = inputRegex.matchEntire(line) ?: error("Wrong aunt spec: $line")
        val (auntNum, properties) = match.destructured
        auntNum.toInt() to properties.split(", ").associate { prop ->
            val propMatch = targetRegex.matchEntire(prop) ?: error("Wrong aut prop spec: $prop")
            val (propName, num) = propMatch.destructured
            propName to num.toInt()
        }
    }
}
