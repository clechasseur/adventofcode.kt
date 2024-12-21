package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.y2022.data.Day4Data

object Day4 {
    private val input = Day4Data.input

    private val pairRegex = """^(\d+)-(\d+),(\d+)-(\d+)$""".toRegex()

    fun part1(): Int = input.lines().map { it.toElfPair() }.count { it.oneElfIsUseless }

    fun part2(): Int = input.lines().map { it.toElfPair() }.count { it.elvesOverlap }

    private data class ElfPair(val elf1: IntRange, val elf2: IntRange) {
        val oneElfIsUseless: Boolean
            get() = (elf1.first >= elf2.first && elf1.last <= elf2.last) ||
                    (elf2.first >= elf1.first && elf2.last <= elf1.last)

        val elvesOverlap: Boolean
            get() = elf1.first in elf2 || elf1.last in elf2 || elf2.first in elf1 || elf2.last in elf1
    }

    private fun String.toElfPair(): ElfPair {
        val match = pairRegex.matchEntire(this) ?: error("Wrong elf pair: $this")
        val (f1, l1, f2, l2) = match.destructured
        return ElfPair(f1.toInt()..l1.toInt(), f2.toInt()..l2.toInt())
    }
}
