object Day7 {
    private val input = listOf(
            "<REDACTED>"
    )

    private val abbaMustMatch = """([a-z])(?!\1)([a-z])\2\1""".toRegex()
    private val abbaMustNotMatch = """\[[a-z]*([a-z])(?!\1)([a-z])\2\1[a-z]*]""".toRegex()

    private val abababMustMatch = """([a-z])(?!\1)([a-z])\1.*;.*\2\1\2""".toRegex()

    fun part1() = input.filter { abbaMustMatch.containsMatchIn(it) && !abbaMustNotMatch.containsMatchIn(it) }.count()

    fun part2() = input.filter { abababMustMatch.containsMatchIn(it.supernetHypernet()) }.count()

    private fun String.supernetHypernet(): String {
        val supernets = mutableListOf<String>()
        val hypernets = mutableListOf<String>()
        var i = indices.first
        while (i in indices) {
            val bracketPos = indexOf('[', startIndex = i)
            if (bracketPos == -1) {
                supernets.add(substring(i..indices.last))
                i = indices.last + 1
            } else {
                supernets.add(substring(i until bracketPos))
                val closingBracketPos = indexOf(']', startIndex = bracketPos + 1)
                hypernets.add(substring((bracketPos + 1) until closingBracketPos))
                i = closingBracketPos + 1
            }
        }
        return "${supernets.joinToString(",")};${hypernets.joinToString(",")}"
    }
}
