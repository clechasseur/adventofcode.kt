object Day1 {
    private val input = """
        <REDACTED>
    """.trimIndent().lineSequence().map { it.toLong() }.toList()

    fun part1() = input.asSequence().map { it.fuelReq() }.sum()

    fun part2() = input.asSequence().flatMap {
        module -> generateSequence(module.fuelReq()) { it.fuelReq() }.takeWhile { it > 0 }
    }.sum()

    private fun Long.fuelReq() = this / 3 - 2
}
