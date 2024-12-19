object Day9 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    fun part1() = parse(input.iterator()).totalScore

    fun part2() = parse(input.iterator()).totalGarbageSize

    private fun parse(stream: CharIterator): Group {
        require(stream.nextChar() == '{') { "Invalid stream start" }
        val group = parseGroup(stream, null)
        require(!stream.hasNext()) { "Stream should be empty" }
        return group
    }

    private fun parseGroup(stream: CharIterator, parent: Group?): Group {
        val group = Group()
        group.parent = parent
        var c = stream.nextChar()
        while (c != '}') {
            when (c) {
                '{' -> group.children.add(parseGroup(stream, group))
                '<' -> group.garbageSize += skipGarbage(stream)
                ',' -> Unit
                else -> error("Invalid stream content: $c")
            }
            c = stream.nextChar()
        }
        return group
    }

    private fun skipGarbage(stream: CharIterator): Int {
        var size = 0
        var c = stream.nextChar()
        while (c != '>') {
            if (c == '!') {
                stream.nextChar()
            } else {
                size++
            }
            c = stream.nextChar()
        }
        return size
    }

    private class Group {
        var parent: Group? = null
        val children = mutableListOf<Group>()
        var garbageSize = 0

        val score: Int
            get() = (parent?.score ?: 0) + 1

        val totalScore: Int
            get() = score + children.map { it.totalScore }.sum()

        val totalGarbageSize: Int
            get() = garbageSize + children.map { it.totalGarbageSize }.sum()
    }
}
