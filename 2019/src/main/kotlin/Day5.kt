import org.clechasseur.adventofcode2019.IntcodeComputer

object Day5 {
    private val input = listOf<Long>() // <REDACTED>

    fun part1(): Int {
        val computer = IntcodeComputer(input, 1)
        val output = computer.readAllOutput()
        require(output.dropLast(1).all { it == 0L }) { "Some tests do not pass" }
        return output.last().toInt()
    }

    fun part2() = IntcodeComputer(input, 5).readOutput()
}
