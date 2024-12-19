object Day8 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val instructionRegex = Regex("""^([a-z]+) (inc|dec) (-?\d+) if ([a-z]+) ([<>!=]+) (-?\d+)$""")
    private val instructions = input.lineSequence().map { it.toInstruction() }.toList()

    fun part1() = mutableMapOf<String, Int>().apply {
        instructions.forEach { it(this) }
    }.asSequence().map { it.value }.max()!!

    fun part2(): Int {
        val registers = mutableMapOf<String, Int>()
        return instructions.map {
            it(registers)
            registers.asSequence().map { it.value }.max()!!
        }.max()!!
    }

    private enum class Comparison(val code: String) {
        EQUAL_TO("=="),
        NOT_EQUAL_TO("!="),
        LESS_THAN("<"),
        LESS_THAN_OR_EQUAL_TO("<="),
        GREATER_THAN(">"),
        GREATER_THAN_OR_EQUAL_TO(">=")
    }

    private data class Instruction(val target: String, val value: Int,
                                   val cond: String, val cmp: Comparison, val cmpValue: Int) {

        operator fun invoke(registers: MutableMap<String, Int>) {
            val condValue = registers.getOrDefault(cond, 0)
            val condition = when (cmp) {
                Comparison.EQUAL_TO -> condValue == cmpValue
                Comparison.NOT_EQUAL_TO -> condValue != cmpValue
                Comparison.LESS_THAN -> condValue < cmpValue
                Comparison.LESS_THAN_OR_EQUAL_TO -> condValue <= cmpValue
                Comparison.GREATER_THAN -> condValue > cmpValue
                Comparison.GREATER_THAN_OR_EQUAL_TO -> condValue >= cmpValue
            }
            if (condition) {
                registers[target] = registers.getOrDefault(target, 0) + value
            }
        }
    }

    private fun String.toComparison() = Comparison.values().find { it.code == this } ?: error("Invalid comparison code")
    private fun String.toInstruction(): Instruction {
        val match = instructionRegex.matchEntire(this) ?: error("Invalid instruction")
        var value = match.groupValues[3].toInt()
        if (match.groupValues[2] == "dec") {
            value = -value
        }
        return Instruction(
            target = match.groupValues[1],
            value = value,
            cond = match.groupValues[4],
            cmp = match.groupValues[5].toComparison(),
            cmpValue = match.groupValues[6].toInt()
        )
    }
}
