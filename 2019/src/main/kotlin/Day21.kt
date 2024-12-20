import org.clechasseur.adventofcode2019.Day21Data
import org.clechasseur.adventofcode2019.IntcodeComputer

object Day21 {
    private val input = Day21Data.input

    fun part1(): Long {
        return runProgram("""
            <REDACTED>
        """.trimIndent())
    }

    fun part2(): Long {
        return runProgram("""
            <REDACTED>
        """.trimIndent())
    }

    private fun runProgram(program: String): Long {
        val droid = IntcodeComputer(input)
        droid.addAsciiInput(program)

        val output = droid.readAllOutput()
        if (output.last() > Char.MAX_VALUE.toLong()) {
            return output.last()
        }
        output.forEach { print(it.toChar()) }
        error("Did not make it across")
    }
}
