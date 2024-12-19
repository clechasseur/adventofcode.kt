import org.clechasseur.adventofcode2016.Day9Data

object Day9 {
    private const val input = Day9Data.input

    private val markerRegex = """\((\d+)x(\d+)\)""".toRegex()

    fun part1() = input.decompressedLength(false)

    fun part2() = input.decompressedLength(true)

    private fun String.decompressedLength(recursive: Boolean): Long {
        var total = 0L
        var i = 0
        while (i in indices) {
            val openParIdx = indexOf('(', startIndex = i)
            i = if (openParIdx >= 0) {
                total += openParIdx - i
                val closeParIdx = indexOf(')', startIndex = openParIdx)
                val markerMatch = markerRegex.matchEntire(substring(openParIdx..closeParIdx))!!
                val (seqLen, times) = markerMatch.destructured
                total += if (recursive) {
                    val seq = substring((closeParIdx + 1)..(closeParIdx + seqLen.toInt()))
                    val sublen = seq.decompressedLength(true)
                    sublen * times.toLong()
                } else {
                    seqLen.toLong() * times.toLong()
                }
                closeParIdx + seqLen.toInt() + 1
            } else {
                total += length - i
                length
            }
        }
        return total
    }
}
