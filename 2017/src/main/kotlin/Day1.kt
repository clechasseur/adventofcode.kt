object Day1 {
    private const val input = "<REDACTED>"

    fun part1() = sumOfMatching(1)

    fun part2() = sumOfMatching(input.length / 2)

    private fun sumOfMatching(jump: Int) = input.indices.mapNotNull { idx ->
        if (input.jumpFor(idx, jump) == input[idx]) input[idx].toString().toInt() else null
    }.sum()

    private fun String.jumpFor(idx: Int, jump: Int): Char = this[(idx + jump) % length]
}
