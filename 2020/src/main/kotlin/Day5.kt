import kotlin.math.pow

object Day5 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    fun part1(): Int = input.lineSequence().map { BoardingPass(it).seatId }.max()!!

    fun part2(): Int {
        val passes = input.lines().map { BoardingPass(it) }
        (1..126).forEach { row ->
            (0..7).forEach { column ->
                if (passes.find { it.row == row && it.column == column } == null) {
                    return row * 8 + column
                }
            }
        }
        error("Could not find seat")
    }

    private class BoardingPass(code: String) {
        val row: Int = code.substring(0..6).decodeBinarySpacePartition('F', 'B')
        val column: Int = code.substring(7..9).decodeBinarySpacePartition('L', 'R')
        val seatId: Int = row * 8 + column
    }

    private fun String.decodeBinarySpacePartition(low: Char, high: Char): Int {
        var lowNum = 0
        var highNum = 2.0.pow(length).toInt() - 1
        forEach { c -> when (c) {
            low -> highNum -= (highNum - lowNum + 1) / 2
            high -> lowNum += (highNum - lowNum + 1) / 2
            else -> error("Wrong encoded char: $c")
        } }
        require(lowNum == highNum) { "Wrong encoding: $this" }
        return lowNum
    }
}