import org.clechasseur.adventofcode2020.Pt

object Day11 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val directions = listOf(Pt(-1, -1), Pt(-1, 0), Pt(-1, 1),
        Pt(0, -1), Pt(0, 1), Pt(1, -1), Pt(1, 0), Pt(1, 1))

    fun part1(): Int {
        var state = input.lines().map { it.toList() }
        var same: Boolean
        do {
            val newState = state.indices.map { y ->
                state[y].indices.map { x ->
                    nextGen(state, Pt(x, y))
                }
            }
            same = state == newState
            state = newState
        } while (!same)
        return state.map { line -> line.count { it == '#' } }.sum()
    }

    fun part2(): Int {
        var state = input.lines().map { it.toList()  }
        var same: Boolean
        do {
            val newState = state.indices.map { y ->
                state[y].indices.map { x ->
                    nextGen2(state, Pt(x, y))
                }
            }
            same = state == newState
            state = newState
        } while (!same)
        return state.map { line -> line.count { it == '#' } }.sum()
    }

    private fun nextGen(map: List<List<Char>>, pt: Pt): Char = when (map[pt.y][pt.x]) {
        'L' -> if (neighbours(map, pt).none { it == '#' }) '#' else 'L'
        '#' -> if (neighbours(map, pt).count { it == '#' } >= 4) 'L' else '#'
        else -> map[pt.y][pt.x]
    }

    private fun nextGen2(map: List<List<Char>>, pt: Pt): Char = when (map[pt.y][pt.x]) {
        'L' -> if (seenFrom(map, pt).none { it == '#' }) '#' else 'L'
        '#' -> if (seenFrom(map, pt).count { it == '#'} >= 5) 'L' else '#'
        else -> map[pt.y][pt.x]
    }

    private fun neighbours(map: List<List<Char>>, pt: Pt): List<Char> = directions.map { map.safeGet(pt + it) }

    private fun seenFrom(map: List<List<Char>>, pt: Pt): List<Char> = directions.map { dir ->
        var curPt = pt + dir
        var seen = '.'
        while (seen == '.' && curPt.y in map.indices && curPt.x in map[0].indices) {
            seen = map[curPt.y][curPt.x]
            curPt += dir
        }
        seen
    }

    private fun List<List<Char>>.safeGet(pt: Pt) =
        if (pt.y in indices && pt.x in this[0].indices) this[pt.y][pt.x] else '.'
}
