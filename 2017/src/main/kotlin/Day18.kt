import org.clechasseur.adventofcode2017.DuetInterpreter

object Day18 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    fun part1() = DuetInterpreter(input, false).recovered ?: error("Could not recover a sound")

    fun part2() = DuetInterpreter(input, true).getSndCount(1)
}
