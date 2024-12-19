object Day16 {
    private const val input = "<REDACTED>"

    fun part1(): String = generateData(272).checksum

    fun part2(): String = generateData(35651584).checksum

    private fun generateData(size: Int): String = generateSequence(input) { a ->
        val b = a.reversed().replace('0', 'x').replace('1', '0').replace('x', '1')
        "${a}0$b"
    }.dropWhile { it.length < size }.first().substring(0 until size)

    private val String.checksum: String
        get() = generateSequence(this) { d ->
            d.zipWithNext().mapIndexed { i, (a, b) ->
                if (i % 2 == 0) {
                    if (a == b) '1' else '0'
                } else {
                    null
                }
            }.filterNotNull().joinToString("")
        }.drop(1).dropWhile { it.length % 2 == 0 }.first()
}
