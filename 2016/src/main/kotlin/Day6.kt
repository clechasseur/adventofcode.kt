import org.clechasseur.adventofcode2016.pivot

object Day6 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    fun part1() = input.lines().map { it.toList() }.pivot().map { posList ->
        posList.maxBy { candidate -> posList.count { c -> c == candidate } }!!
    }.joinToString("")

    fun part2() = input.lines().map { it.toList() }.pivot().map { posList ->
        posList.minBy { candidate -> posList.count { c -> c == candidate } }!!
    }.joinToString("")
}
