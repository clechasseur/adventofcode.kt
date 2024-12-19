object Day3 {
    private val input = """
  <REDACTED>
    """.trimIndent()

    private val triangles = input.lineSequence().map { line ->
        line.trimStart().split("\\s+".toRegex()).map { it.toInt() }
    }.toList()

    private val verticalTriangles = triangles.chunked(3).flatMap { triangles ->
        listOf(
                listOf(triangles[0][0], triangles[1][0], triangles[2][0]),
                listOf(triangles[0][1], triangles[1][1], triangles[2][1]),
                listOf(triangles[0][2], triangles[1][2], triangles[2][2])
        )
    }

    fun part1() = triangles.filter { it.possibleTriangle }.count()

    fun part2() = verticalTriangles.filter { it.possibleTriangle }.count()

    private val List<Int>.possibleTriangle: Boolean get() {
        val ordered = sorted()
        return ordered[0] + ordered[1] > ordered[2]
    }
}
