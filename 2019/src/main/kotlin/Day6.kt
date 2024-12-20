object Day6 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val orbits = input.lineSequence().map { it.split(')') }.map { it[1] to it[0] }.toMap()

    fun part1() = orbits.keys.map { pathToCom(it).size - 1 }.sum()

    fun part2(): Int {
        val myPath = pathToCom("YOU").reversed()
        val sanPath = pathToCom("SAN").reversed()
        val commonLen = (myPath zip sanPath).takeWhile { it.first == it.second }.size
        return (myPath.size - commonLen) + (sanPath.size - commonLen) - 2
    }

    private fun pathToCom(obj: String): List<String> {
        val path = mutableListOf(obj)
        while (path.last() != "COM") {
            path.add(orbits[path.last()] ?: error("Cannot find orbit for ${path.last()}"))
        }
        return path
    }
}
