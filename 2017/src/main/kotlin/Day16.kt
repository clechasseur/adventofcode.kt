object Day16 {
    private const val input = "<REDACTED>"

    fun part1() = applyMoves(('a'..'p').toList()).joinToString("")

    fun part2(): String {
        val initial = ('a'..'p').toList()
        val loopSize = generateSequence(initial) { applyMoves(it) }.drop(1).takeWhile { it != initial }.count() + 1
        val remaining = 1_000_000_000 % loopSize
        return generateSequence(initial) { applyMoves(it) }.drop(1).take(remaining).last().joinToString("")
    }

    private fun applyMoves(programs: List<Char>): List<Char> {
        val movesIt = input.split(',').iterator()
        return generateSequence(programs) { prevPrograms ->
            when (movesIt.hasNext()) {
                true ->  {
                    val move = movesIt.next()
                    when (move.first()) {
                        's' -> {
                            val spin = move.drop(1).toInt()
                            prevPrograms.takeLast(spin) + prevPrograms.dropLast(spin)
                        }
                        'x' -> {
                            val (x, y) = move.drop(1).split('/').map { it.toInt() }
                            prevPrograms.toMutableList().apply { swap(x, y) }
                        }
                        'p' -> {
                            val (x, y) = move.drop(1).split('/').map { prevPrograms.indexOf(it.first()) }
                            prevPrograms.toMutableList().apply { swap(x, y) }
                        }
                        else -> error("Wrong move format: $move")
                    }
                }
                false -> null
            }
        }.last()
    }

    private fun <T> MutableList<T>.swap(x: Int, y: Int) {
        val tmp = this[x]
        this[x] = this[y]
        this[y] = tmp
    }
}
