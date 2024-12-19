import org.clechasseur.adventofcode2016.Day20Data
import kotlin.math.max
import kotlin.math.min

object Day20 {
    private val input = Day20Data.input

    fun part1(): Long = mergeBlockList(getBlockList()).sortedBy { it.first }[0].last + 1L

    fun part2(): Long = 4294967296L - mergeBlockList(getBlockList()).map { it.last - it.first + 1 }.sum()

    private fun getBlockList(): List<LongRange> = input.lines().map {
        val (from, to) = it.split('-')
        (from.toLong())..(to.toLong())
    }

    private fun mergeBlockList(blockList: List<LongRange>): List<LongRange> {
        var curBlockList = blockList
        val mergedBlockList = mutableListOf<LongRange>()
        var mergedSomething = true
        while (mergedSomething) {
            mergedSomething = false
            while (curBlockList.isNotEmpty()) {
                val block = curBlockList[0]
                val (toMerge, rest) = curBlockList.partition {
                    it.first in block || it.last in block ||
                            block.first in it || block.last in it ||
                            it.first == block.last + 1 || it.last == block.first - 1
                }
                mergedSomething = mergedSomething || toMerge.size > 1
                val merged = toMerge.reduce { acc, r ->
                    min(acc.first, r.first)..max(acc.last, r.last)
                }
                mergedBlockList.add(merged)
                curBlockList = rest
            }
            if (mergedSomething) {
                curBlockList = mergedBlockList.toList()
                mergedBlockList.clear()
            }
        }
        return mergedBlockList
    }
}
