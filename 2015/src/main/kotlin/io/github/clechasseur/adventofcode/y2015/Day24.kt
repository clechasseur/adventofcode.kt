package io.github.clechasseur.adventofcode.y2015

import io.github.clechasseur.adventofcode.y2015.data.Day24Data

object Day24 {
    private val input = Day24Data.input

    fun part1(): Long = minQuantumEntanglement(input.sum() / 3L)

    fun part2(): Long = minQuantumEntanglement(input.sum() / 4L)

    private fun minQuantumEntanglement(neededWeight: Long): Long = (2..input.size).asSequence().flatMap { size ->
        subPermutations(size).filter {
            it.weight == neededWeight
        }.sortedBy {
            it.quantumEntanglement
        }
    }.first().quantumEntanglement

    private fun subPermutations(size: Int, soFar: List<Long> = emptyList()): Sequence<List<Long>> = if (size == 0) {
        sequenceOf(soFar)
    } else {
        input.asSequence().filter { !soFar.contains(it) }.flatMap { element ->
            subPermutations(size - 1, soFar + element)
        }
    }

    private val List<Long>.weight: Long
        get() = sum()

    private val List<Long>.quantumEntanglement: Long
        get() = reduceOrNull { acc, l -> acc * l } ?: 0L
}
