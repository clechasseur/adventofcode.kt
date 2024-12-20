import org.clechasseur.adventofcode2019.Day25Data
import org.clechasseur.adventofcode2019.IntcodeComputer

object Day25 {
    private val input = Day25Data.input

    fun part1() {
        val droid = IntcodeComputer(input)
        while (true) {
            droid.printAsciiOutput()
            droid.addAsciiInput(readLine() ?: break)
            droid.addAsciiInput("\n")
        }
    }
}

fun main(vararg args: String) {
    Day25.part1()
}
