object Day5 {
    private val input = """
        <REDACTED>
    """.trimIndent().lineSequence().map { it.toInt() }.toList()

    fun part1() = solve(input)

    fun part2() = solve(input, 3)

    private fun solve(maze: List<Int>, reverseThreshold: Int? = null): Int {
        val writableMaze = maze.toMutableList()
        var steps = 0
        var idx = 0
        while (idx in writableMaze.indices) {
            val nextIdx = idx + writableMaze[idx]
            if (reverseThreshold != null && writableMaze[idx] >= reverseThreshold) {
                writableMaze[idx]--
            } else {
                writableMaze[idx]++
            }
            idx = nextIdx
            steps++
        }
        return steps
    }
}
