object Day2 {
    private val input = listOf(
        "<REDACTED>"
    )

    private val passwordRegex = """^(\d+)-(\d+) ([a-z]): ([a-z]+)$""".toRegex()

    fun part1(): Int = input.map { it.toPassword() }.filter { it.valid }.count()

    fun part2(): Int = input.map { it.toPassword() }.filter { it.reallyValid }.count()

    private class Password(val policyRange: IntRange, val policyLetter: Char, val password: String) {
        val valid: Boolean
            get() = password.filter { it == policyLetter }.count() in policyRange

        val reallyValid: Boolean
            get() {
                return (password.safeGet(policyRange.first - 1) == policyLetter) xor
                        (password.safeGet(policyRange.last - 1) == policyLetter)
            }
    }

    private fun String.toPassword(): Password {
        val match = passwordRegex.matchEntire(this) ?: error("$this does not match password regex")
        return Password(
            match.groupValues[1].toInt()..match.groupValues[2].toInt(),
            match.groupValues[3].first(),
            match.groupValues[4]
        )
    }

    private fun String.safeGet(p: Int): Char = if (length > p) this[p] else '-'
}