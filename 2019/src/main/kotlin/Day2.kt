import org.clechasseur.adventofcode2019.IntcodeComputer
import org.clechasseur.adventofcode2019.math.generatePairSequence

object Day2 {
    private val input = listOf<Long>() // <REDACTED>

    private const val target = 19690720

    fun part1() = run(12, 2)

    fun part2(): Int {
        val (noun, verb) = generatePairSequence(0..99, 0..99).map {
            it to run(it.first, it.second)
        }.filter { it.second == target }.first().first
        return 100 * noun + verb
    }

    private fun run(noun: Int, verb: Int): Int {
        return IntcodeComputer(input.toMutableList().apply {
            this[1] = noun.toLong()
            this[2] = verb.toLong()
        }).memory[0].toInt()
    }
}
