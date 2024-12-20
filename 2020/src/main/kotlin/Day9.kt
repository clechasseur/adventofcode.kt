import org.clechasseur.adventofcode2020.Day9Data
import org.clechasseur.adventofcode2020.math.generatePairSequence

object Day9 {
    private val input = Day9Data.input

    fun part1(): Long = invalidNumber().first

    fun part2(): Long {
        val (wrong, wrongIdx) = invalidNumber()
        val candidates = 0 until wrongIdx
        generatePairSequence(candidates, candidates).forEach { (idx1, idx2) ->
            if (idx1 < idx2) {
                val subRange = input.subList(idx1, idx2)
                if (subRange.sum() == wrong) {
                    return subRange.min()!! + subRange.max()!!
                }
            }
        }
        error("Could not find encryption weakness")
    }

    private fun invalidNumber(): Pair<Long, Int> {
        input.indices.drop(25).forEach { idx ->
            val prev25indexes = (idx - 25) until idx
            if (generatePairSequence(prev25indexes, prev25indexes).none { it.addsUp(idx) }) {
                return input[idx] to idx
            }
        }
        error("Could not find candidate number")
    }

    private fun Pair<Int, Int>.addsUp(idx: Int) = first != second && input[first] + input[second] == input[idx]
}