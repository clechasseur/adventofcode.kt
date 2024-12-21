package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.y2022.data.Day25Data
import kotlin.math.pow

object Day25 {
    private val input = Day25Data.input

    fun part1(): String = input.lines().map { SnafuNumber(it) }.sum().asSnafu

    private val snafuValues = mapOf('2' to 2L, '1' to 1L, '0' to 0L, '-' to -1L, '=' to -2L)

    private data class SnafuNumber(val asSnafu: String) {
        constructor(valueAsDecimal: Long) : this(valueAsDecimal.snafuify())

        val asDecimal: Long
            get() = asSnafu.reversed().withIndex().sumOf { (p, c) ->
                snafuValues[c]!! * 5.0.pow(p).toLong()
            }
    }

    private fun Long.snafuify(): String {
        var buf = toString(5).reversed()
        var carry = 0
        var res = ""
        while (buf.isNotEmpty()) {
            val c = buf.first().toString().toInt() + carry
            buf = buf.drop(1)
            carry = 0

            res += when (c) {
                in 0..2 -> c.toString()
                3 -> {
                    carry = 1
                    "="
                }
                4 -> {
                    carry = 1
                    "-"
                }
                else -> {
                    carry = c - 4
                    (c - 5).toString()
                }
            }
        }
        if (carry != 0) {
            res += carry.toString()
        }
        return res.reversed()
    }

    private fun Collection<SnafuNumber>.sum(): SnafuNumber = SnafuNumber(sumOf { it.asDecimal })
}
