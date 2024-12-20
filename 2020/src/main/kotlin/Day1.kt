object Day1 {
    private val input = listOf(
        0 // <REDACTED>
    )

    fun part1(): Int {
        return input.indices.map { x ->
            input.indices.filter { it != x }.map { y ->
                if (input[x] + input[y] == 2020) input[x] * input[y] else 0
            }.max()!!
        }.max()!!
    }

    fun part2(): Int {
        return input.indices.map { x ->
            input.indices.filter { it != x }.map { y ->
                input.indices.filter { it != x && it != y }.map { z ->
                    if (input[x] + input[y] + input[z] == 2020) input[x] * input[y] * input[z] else 0
                }.max()!!
            }.max()!!
        }.max()!!
    }
}
