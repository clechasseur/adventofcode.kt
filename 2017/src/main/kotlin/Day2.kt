import org.clechasseur.adventofcode2017.math.permutations

object Day2 {
    private val input = """
        <REDACTED>
    """.trimIndent().lineSequence().map { line -> line.split('\t').map { it.toInt() } }.toList()

    fun part1() = input.sheetChecksum()

    fun part2() = input.sheetDivisions()

    private fun List<Int>.lineChecksum(): Int {
        val ordered = sorted()
        return ordered.last() - ordered.first()
    }

    private fun List<List<Int>>.sheetChecksum(): Int = map { it.lineChecksum() }.sum()

    private fun List<Int>.lineDivision(): Int = indices.asSequence().flatMap { idx ->
        indices.asSequence().mapNotNull { if (it != idx) (this[idx] to this[it]).sorted() else null }
    }.filter { it.second % it.first == 0 }.map { it.second / it.first }.first()

    private fun List<List<Int>>.sheetDivisions(): Int = map { it.lineDivision() }.sum()

    private fun <T: Comparable<T>> Pair<T, T>.sorted() = if (first > second) second to first else this
}
