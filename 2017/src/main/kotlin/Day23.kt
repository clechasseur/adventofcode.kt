import org.clechasseur.adventofcode2017.DuetInterpreter

object Day23 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    fun part1() = DuetInterpreter(input, false).getMulCount(0)

    fun part2() = (0L..0L step 17).filter { num -> // <REDACTED>
        (2L..(num / 2L)).any { num % it == 0L }
    }.count().toLong()
}
