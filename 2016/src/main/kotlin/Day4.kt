object Day4 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val rooms = input.lineSequence().map { it.toRoom() }.toList()

    fun part1() = rooms.filter { it.real }.sumBy { it.sectorId }

    fun part2() = rooms.single { it.real && it.decryptedName.contains("northpole") }.sectorId

    private data class Room(val name: String, val sectorId: Int, val checksum: List<Char>) {
        companion object {
            val parsingRegex = """^([a-z-]+)-(\d+)\[([a-z]{5})]$""".toRegex()
        }

        val real: Boolean
            get() = name.asSequence().filter { it != '-' }.sorted().groupBy { it }.map {
                (c, m) -> c to m.size
            }.sortedWith(Comparator { o1, o2 ->
                var cmp = o2.second - o1.second
                if (cmp == 0) {
                    cmp = o1.first - o2.first
                }
                cmp
            }).take(5).map { it.first } == checksum

        val decryptedName: String
            get() = name.map { c ->
                if (c == '-') {
                    ' '
                } else {
                    c.rotateInAlphabet(sectorId)
                }
            }.joinToString("")
    }

    private fun String.toRoom(): Room {
        val match = Room.parsingRegex.matchEntire(this) ?: error("Wrong room format: $this")
        return Room(
                name = match.groupValues[1],
                sectorId = match.groupValues[2].toInt(),
                checksum = match.groupValues[3].toList()
        )
    }

    private fun Char.rotateInAlphabet(by: Int): Char = (((this - 'a') + by) % 26 + 'a'.toInt()).toChar()
}
