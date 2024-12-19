package io.github.clechasseur.adventofcode.y2015

import io.github.clechasseur.adventofcode.util.md5
import java.math.BigInteger

object Day4 {
    private const val input = "<REDACTED>";

    fun part1(): Int = lowestMd5("00000", 1000000)

    fun part2(): Int = lowestMd5("000000", 10000000)

    private fun lowestMd5(mustStartWith: String, howMany: Int): Int = generateSequence(1) {
        it + 1
    }.map {
        it to md5("$input$it")
    }.take(howMany).filter {
        it.second.startsWith(mustStartWith)
    }.minBy {
        BigInteger(it.second, 16)
    }.first
}
