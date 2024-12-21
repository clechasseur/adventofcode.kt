package io.github.clechasseur.adventofcode2021

import io.github.clechasseur.adventofcode2021.data.Day8Data
import io.github.clechasseur.adventofcode2021.util.permutations

object Day8 {
    private val data = Day8Data.data

    private val lineInfos = data.lines().map { it.toLineInfo() }

    fun part1(): Int = lineInfos.flatMap { it.outputValues }.count { it.length in listOf(2, 3, 4, 7) }

    fun part2(): Int = lineInfos.map { lineInfo ->
        crossedWiresPermutations.asSequence().mapNotNull { wiresCrossing ->
            lineInfo.toDigitsLine(wiresCrossing)
        }.single()
    }.sumOf { it.display }

    private data class LineInfo(val signalPatterns: List<String>, val outputValues: List<String>)

    private fun String.toLineInfo(): LineInfo {
        val (patternsPart, outputPart) = split('|')
        return LineInfo(
            patternsPart.trim().split("\\s+".toRegex()),
            outputPart.trim().split("\\s+".toRegex())
        )
    }

    private val digits = mapOf(
        "abcefg" to 0,
        "cf" to 1,
        "acdeg" to 2,
        "acdfg" to 3,
        "bcdf" to 4,
        "abdfg" to 5,
        "abdefg" to 6,
        "acf" to 7,
        "abcdefg" to 8,
        "abcdfg" to 9,
    )

    private val wiresPermutations = permutations("abcdefg".toList())

    private val crossedWiresPermutations = wiresPermutations.map { crossed ->
        crossed.zip("abcdefg".toList()).toMap()
    }

    private data class DigitsLine(val signalPatterns: List<Int>, val outputValues: List<Int>) {
        val display: Int
            get() = outputValues.joinToString("") { it.toString() }.toInt()
    }

    private fun LineInfo.toDigitsLine(wiresCrossing: Map<Char, Char>): DigitsLine? {
        val digitsSignalPatterns = signalPatterns.map { pattern ->
            digits[pattern.toList().map { wiresCrossing[it]!! }.sorted().joinToString("")] ?: return null
        }
        val digitsOutputValues = outputValues.map { value ->
            digits[value.toList().map { wiresCrossing[it]!! }.sorted().joinToString("")] ?: return null
        }
        return DigitsLine(digitsSignalPatterns, digitsOutputValues)
    }
}
