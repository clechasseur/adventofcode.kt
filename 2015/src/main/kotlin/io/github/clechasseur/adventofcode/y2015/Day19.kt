package io.github.clechasseur.adventofcode.y2015

import io.github.clechasseur.adventofcode.y2015.data.Day19Data
import kotlin.math.min

object Day19 {
    private val transforms = Day19Data.transforms
    private const val molecule = Day19Data.molecule

    private val transformRegex = """^(\w+) => (\w+)$""".toRegex()

    fun part1(): Int = transforms.toTransformMap().allTransforms(molecule).distinct().count()

    fun part2(): Int {
        val transformsList = transforms.toTransformMap().reverse()
        return generateSequence(State(molecule)) {
            it.next(transformsList) ?: error("Could not find path to e")
        }.dropWhile {
            it.steps.last().mol != "e"
        }.first().steps.size - 1
    }

    private fun String.toTransformMap(): Map<Regex, List<String>> = lines().map { line ->
        val match = transformRegex.matchEntire(line) ?: error("Wrong transform: $line")
        match.groupValues[1] to match.groupValues[2]
    }.fold(mutableMapOf<Regex, MutableList<String>>()) { acc, (from, to) ->
        acc.computeIfAbsent(from.toRegex()) { mutableListOf() }.add(to)
        acc
    }

    private fun Map<Regex, List<String>>.reverse(): List<Pair<Regex, String>> = flatMap { (from, toList) ->
        toList.map { to -> to.toRegex() to from.pattern }
    }.sortedByDescending { it.first.pattern.length }

    private fun Map<Regex, List<String>>.allTransforms(mol: String): Sequence<String> = asSequence().flatMap { (from, toList) ->
        toList.asSequence().flatMap { to ->
            from.findAll(mol).map { match ->
                mol.substring(0, match.range.first) + to + mol.substring(match.range.last + 1, mol.length)
            }
        }
    }

    private class Step(val mol: String, val path: List<Pair<Int, Int>>) {
        fun next(transforms: List<Pair<Regex, String>>, startingAt: Pair<Int, Int>?): Step? = transforms.asSequence().mapIndexed { i, (f, t) ->
            val match = f.find(
                mol,
                min(if (i == 0 && startingAt != null) startingAt.second + f.pattern.length else 0, mol.length)
            )
            if (match != null) {
                val rep = mol.substring(0, match.range.first) + t + mol.substring(match.range.last + 1, mol.length)
                Step(rep, path + (i to match.range.first))
            } else null
        }.drop(startingAt?.first ?: 0).filterNotNull().firstOrNull()
    }

    private class State(val steps: List<Step>) {
        constructor(initialMol: String) : this(listOf(Step(initialMol, emptyList())))

        fun next(transforms: List<Pair<Regex, String>>, startingAt: Pair<Int, Int>? = null): State? = if (steps.isEmpty()) null else {
            val lastStep = steps.last()
            val stepNext = lastStep.next(transforms, startingAt)
            if (stepNext != null) {
                State(steps + stepNext)
            } else {
                val stepsBack = steps.subList(0, steps.indices.last)
                State(stepsBack).next(transforms, lastStep.path.last())
            }
        }
    }
}
