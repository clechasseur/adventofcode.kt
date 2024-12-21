package io.github.clechasseur.adventofcode2021

import io.github.clechasseur.adventofcode2021.data.Day10Data

object Day10 {
    private val data = Day10Data.data

    fun part1(): Int = data.lines().map { Line(it).corruptionScore }.filter { it != 0 }.sum()

    fun part2(): Long {
        val autocompletedLines = data.lines().map { Line(it) }.filter {
            it.corruptionScore == 0
        }.sortedBy {
            it.autocompleteScore
        }
        return autocompletedLines[autocompletedLines.size / 2].autocompleteScore
    }

    private val opposites = mapOf(
        '(' to ')',
        '[' to ']',
        '{' to '}',
        '<' to '>',
    )

    private val corruptedScores = mapOf(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137,
    )

    private val autocompletedScores = mapOf(
        '(' to 1L,
        '[' to 2L,
        '{' to 3L,
        '<' to 4L,
    )

    private data class Line(val content: String) {
        val corruptionScore: Int by lazy {
            var score = 0
            val stack = ArrayDeque<Char>()
            for (c in content) {
                when (c) {
                    '(', '[', '{', '<' -> stack.addLast(c)
                    ')', ']', '}', '>' -> {
                        val top = stack.removeLastOrNull() ?: error("Invalid line: $content")
                        if (c != opposites[top]!!) {
                            score = corruptedScores[c]!!
                            break
                        }
                    }
                    else -> error("Invalid character: $c in line: $content")
                }
            }
            score
        }

        val autocompleteScore: Long by lazy {
            require(corruptionScore == 0) { "To autocomplete a line, it must not be corrupted" }
            val stack = ArrayDeque<Char>()
            for (c in content) {
                when (c) {
                    '(', '[', '{', '<' -> stack.addFirst(c)
                    ')', ']', '}', '>' -> stack.removeFirstOrNull() ?: error("Invalid line: $content")
                    else -> error("Invalid character: $c in line: $content")
                }
            }
            var score = 0L
            for (c in stack) {
                score *= 5L
                score += autocompletedScores[c]!!
            }
            score
        }
    }
}
