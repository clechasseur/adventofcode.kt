object Day4 {
    private val input = """
        <REDACTED>
    """.trimIndent().lineSequence().toList()

    private val invalid = Regex("""^.*\b([a-z]+)\b.+\b\1\b.*$""")

    fun part1() = input.filterNot { invalid.matches(it) }.size

    fun part2() = input.filter { phrase ->
        val words = phrase.split(' ').map { word ->
            word.toList().sorted().joinToString("")
        }
        words.toSet().size == words.size
    }.size
}
