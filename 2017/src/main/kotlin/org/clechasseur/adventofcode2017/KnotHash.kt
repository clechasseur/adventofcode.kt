package org.clechasseur.adventofcode2017

import kotlin.math.ceil

class KnotHash(private val lengths: List<Int>, private val rounds: Int) {
    companion object {
        val suffixLengths = listOf(17, 31, 73, 47, 23)
        const val standardRounds = 64
    }

    constructor(input: String) : this(input.map { it.toInt() } + suffixLengths, standardRounds)

    val sparseHash: List<Int> by lazy {
        val list = (0..255).toMutableList()
        var pos = 0
        var skip = 0
        (0 until rounds).forEach { _ ->
            lengths.forEach { length ->
                (pos until (pos + ceil(length.toDouble() / 2.0).toInt())).forEach { a ->
                    val b = pos + (length - (a - pos)) - 1
                    list.swapIndexes(a % list.size, b % list.size)
                }
                pos += length + skip++
            }
        }
        list
    }

    val denseHash: List<Int> by lazy {
        sparseHash.chunked(16).map { elements -> elements.reduce { acc, i -> acc xor i } }
    }

    override fun toString(): String = denseHash.joinToString("") {
        it.toString(16).padStart(2, '0')
    }

    private fun MutableList<Int>.swapIndexes(a: Int, b: Int) {
        val tmp = this[a]
        this[a] = this[b]
        this[b] = tmp
    }
}
