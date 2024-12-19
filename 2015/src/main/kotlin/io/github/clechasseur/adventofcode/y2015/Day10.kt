package io.github.clechasseur.adventofcode.y2015

object Day10 {
    private const val input = "<REDACTED>"

    fun part1(): Int = generateSequence(input) { it.lookAndSay() }.drop(40).first().length

    fun part2(): Int = generateSequence(input) { it.lookAndSay() }.drop(50).first().length

    private fun String.lookAndSay(): String = """(\d)\1*""".toRegex().replace(this) {
        "${it.value.length}${it.groupValues[1]}"
    }
}
