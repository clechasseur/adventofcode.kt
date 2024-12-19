import org.clechasseur.adventofcode2016.math.generatePairSequence

object Day8 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val rectRegex = """rect (\d+)x(\d+)""".toRegex()
    private val rotateRegex = """rotate ([a-z]+) [xy]=(\d+) by (\d+)""".toRegex()

    private val inputInstructions = input.lines().flatMap { it.toInstructions() }

    fun part1() = blankScreen().apply {
        inputInstructions.forEach { instruction ->
            instruction(this)
        }
    }.map { row ->
        row.count { it }
    }.sum()

    fun part2() = blankScreen().apply {
        inputInstructions.forEach { instruction ->
            instruction(this)
        }
    }.joinToString("\n") { row ->
        row.map { if (it) '#' else ' ' }.joinToString("")
    }

    private fun blankScreen() = MutableList(6) { MutableList(50) { false } }

    private interface Instruction {
        operator fun invoke(screen: MutableList<MutableList<Boolean>>)
    }

    private class Rect(val width: Int, val height: Int) : Instruction {
        override fun invoke(screen: MutableList<MutableList<Boolean>>) {
            generatePairSequence(0 until width, 0 until height).forEach { (x, y) ->
                screen[y][x] = true
            }
        }
    }

    private class ShiftRow(val y: Int) : Instruction {
        override fun invoke(screen: MutableList<MutableList<Boolean>>) {
            val newRow = listOf(screen[y][49]) + screen[y].subList(0, 49)
            screen[y] = newRow.toMutableList()
        }
    }

    private class ShiftColumn(val x: Int): Instruction {
        override fun invoke(screen: MutableList<MutableList<Boolean>>) {
            val new0 = screen[5][x]
            for (y in 5 downTo 1) {
                screen[y][x] = screen[y - 1][x]
            }
            screen[0][x] = new0
        }
    }

    private fun String.toInstructions(): List<Instruction> = when (val rectMatch = rectRegex.matchEntire(this)) {
        null -> {
            val rotateMatch = rotateRegex.matchEntire(this)!!
            val instructions = mutableListOf<Instruction>()
            if (rotateMatch.groupValues[1] == "row") {
                (0 until rotateMatch.groupValues[3].toInt()).forEach { _ ->
                    instructions.add(ShiftRow(rotateMatch.groupValues[2].toInt()))
                }
            } else {
                (0 until rotateMatch.groupValues[3].toInt()).forEach { _ ->
                    instructions.add(ShiftColumn(rotateMatch.groupValues[2].toInt()))
                }
            }
            instructions
        }
        else -> listOf(Rect(rectMatch.groupValues[1].toInt(), rectMatch.groupValues[2].toInt()))
    }
}
