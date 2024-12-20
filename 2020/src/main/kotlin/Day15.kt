object Day15 {
    private val input = listOf(0) // <REDACTED>

    fun part1(): Int = game().drop(2020 - 1).first()

    fun part2(): Int = game().drop(30_000_000 - 1).first()

    private fun game(): Sequence<Int> = sequence {
        val lastSeen = mutableMapOf<Int, List<Int>>()
        var turn = 1
        input.forEach { initial ->
            yield(initial)
            lastSeen[initial] = listOf(turn++)
        }
        var last = input.last()
        while (true) {
            val lastTurns = lastSeen[last]?.filter { it != turn - 1 } ?: listOf()
            val next = if (lastTurns.isNotEmpty()) (turn - 1) - lastTurns.last() else 0
            yield(next)
            lastSeen[next] = (lastSeen[next]?.takeLast(1) ?: listOf()) + turn++
            last = next
        }
    }
}