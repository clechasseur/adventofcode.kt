import org.clechasseur.adventofcode2020.Pt

object Day3 {
    private val input = """
        <REDACTED>
    """.trimIndent().lines()

    fun part1(): Long = checkSlope(Pt(3, 1))

    fun part2(): Long {
        val speeds = listOf(Pt(1, 1), Pt(3, 1), Pt(5, 1), Pt(7, 1), Pt(1, 2))
        return speeds.map { checkSlope(it) }.fold(1L) { acc, l -> acc * l }
    }

    private fun checkSlope(speed: Pt): Long {
        val terrain = Terrain(input)
        return generateSequence(Pt(0, 0)) { it + speed }.takeWhile {
            it.y <= terrain.slopeLength
        }.filter { terrain[it] }.count().toLong()
    }

    private class Terrain(private val topography: List<String>) {
        val slopeLength: Int get() = topography.size

        operator fun get(pt: Pt): Boolean {
            return if (pt.y >= topography.size) false else topography[pt.y][pt.x % topography[0].length] == '#'
        }
    }
}