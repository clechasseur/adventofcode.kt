object Day18 {
    private const val input = "<REDACTED>"

    private val traps = listOf("^^.", ".^^", "^..", "..^")

    fun part1(): Int = numSafeTiles(40)

    fun part2(): Int = numSafeTiles(400_000)

    private fun numSafeTiles(rows: Int) = generateSequence(input) {
        nextRow(it)
    }.take(rows).map { row ->
        row.count { it == '.' }
    }.sum()

    private fun nextRow(row: String): String = row.indices.map { i ->
        if (row.relevant(i).trap) '^' else '.'
    }.joinToString("")

    private fun String.relevant(i: Int): String = "${safeGet(i - 1)}${safeGet(i)}${safeGet(i + 1)}"

    private fun String.safeGet(i: Int): Char = if (i in indices) this[i] else '.'

    private val String.trap: Boolean
        get() = traps.contains(this)
}
