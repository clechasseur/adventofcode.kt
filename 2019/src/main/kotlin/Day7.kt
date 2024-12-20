import org.clechasseur.adventofcode2019.IntcodeComputer
import org.clechasseur.adventofcode2019.math.permutations

object Day7 {
    private val input = listOf<Long>() // <REDACTED>

    fun part1() = permutations(listOf(0, 1, 2, 3, 4)).map { testSetting(it) }.max()!!

    fun part2() = permutations(listOf(5, 6, 7, 8, 9)).map { testSetting(it) }.max()!!

    private fun testSetting(phases: List<Int>): Int {
        val computers = phases.map { phase -> IntcodeComputer(input, phase.toLong()) }
        var output = 0
        while (!computers.last().done) {
            computers.forEach { cpu ->
                cpu.addInput(output.toLong())
                output = cpu.readOutput().toInt()
            }
        }
        return output
    }
}
