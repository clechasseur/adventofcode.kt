package io.github.clechasseur.adventofcode.y2015

object Day11 {
    private const val input = "<REDACTED>"

    private val increasingStraights = ('a'..'x').map { "$it${it + 1}${it + 2}" }
    private val letterPairs = ('a'..'z').map { "$it$it" }.toSet()
    private val forbiddenLetters = "iol".toCharArray()

    fun part1(): String = input.next()

    fun part2(): String = input.next().next()

    private fun String.inc(): String {
        var cidx = 7
        var newValue = this
        while (cidx >= 0) {
            if (newValue[cidx] < 'z') {
                newValue = newValue.substring(0, cidx) + (newValue[cidx] + 1) + newValue.substring(cidx + 1)
                break
            }
            newValue = newValue.substring(0, cidx) + 'a' + newValue.substring(cidx + 1)
            cidx -= 1
        }
        return newValue
    }

    private val String.valid: Boolean
        get() {
            val rule1 = indexOfAny(increasingStraights) >= 0
            val rule2 = indexOfAny(forbiddenLetters) == -1
            val firstLetterPair = findAnyOf(letterPairs)
            val rule3 = firstLetterPair != null &&
                    indexOfAny(letterPairs - firstLetterPair.second) >= 0
            return rule1 && rule2 && rule3
        }

    private fun String.next(): String = generateSequence(this) { it.inc() }.drop(1).filter { it.valid }.first()
}
