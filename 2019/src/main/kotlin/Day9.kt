import org.clechasseur.adventofcode2019.IntcodeComputer

object Day9 {
    private val input = listOf<Long>() // <REDACTED>

    fun part1(): Long {
        val computer = IntcodeComputer(input, 1L)
        val output = computer.readAllOutput()
        require(output.size == 1) { "Malfunctioning opcodes: ${output.dropLast(1).joinToString()}" }
        return output.last()
    }

    fun part2() = IntcodeComputer(input, 2L).readOutput()
}
