package io.github.clechasseur.adventofcode.y2015

object Day25 {
    private const val inputRow = 0 // <REDACTED>
    private const val inputCol = 0 // <REDACTED>

    fun part1(): Long {
        val idx = firstColSeqNum(inputRow + inputCol - 1) + inputCol - 1
        return valueSeq().drop(idx - 1).first()
    }

    private fun firstColSeqNum(row: Int): Int = generateSequence(1 to 1) { (s, i) ->
        (s + i) to (i + 1)
    }.drop(row - 1).first().first

    private fun valueSeq(): Sequence<Long> = generateSequence(20151125L) {
        it * 252533L % 33554393L
    }
}
