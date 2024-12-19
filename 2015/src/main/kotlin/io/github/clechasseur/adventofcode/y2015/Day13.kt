package io.github.clechasseur.adventofcode.y2015

import io.github.clechasseur.adventofcode.util.permutations
import io.github.clechasseur.adventofcode.y2015.data.Day13Data

object Day13 {
    private val input = Day13Data.input

    private val happyRegex = """^(\w+) would (gain|lose) (\d+) happiness units by sitting next to (\w+).$""".toRegex()

    fun part1(): Int {
        val instructions = input.toInstructions()
        val persons = instructions.keys.toList()
        val arrangements = permutations(persons)
        return arrangements.maxOf { it.happiness(instructions) }
    }

    fun part2(): Int {
        val instructions = input.toInstructions()
        instructions.keys.toList().forEach {
            instructions[it]!!["Me"] = 0
            instructions.computeIfAbsent("Me") { mutableMapOf() }[it] = 0
        }
        val persons = instructions.keys.toList()
        val arrangements = permutations(persons)
        return arrangements.maxOf { it.happiness(instructions) }
    }

    private fun List<String>.happiness(instructions: Map<String, Map<String, Int>>): Int =
        (zipWithNext() + (last() to first())).sumOf { it.happiness(instructions) }

    private fun Pair<String, String>.happiness(instructions: Map<String, Map<String, Int>>): Int =
        instructions[first]!![second]!! + instructions[second]!![first]!!

    private fun MutableMap<String, MutableMap<String, Int>>.addInstruction(instruction: String) {
        val match = happyRegex.matchEntire(instruction) ?: error("Wrong instruction: $instruction")
        val (a, swing, i, b) = match.destructured
        computeIfAbsent(a) { mutableMapOf() }[b] = if (swing == "gain") i.toInt() else -i.toInt()
    }

    private fun String.toInstructions(): MutableMap<String, MutableMap<String, Int>> {
        val instructions = mutableMapOf<String, MutableMap<String, Int>>()
        lines().forEach { instructions.addInstruction(it) }
        return instructions
    }
}
