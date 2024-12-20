import org.clechasseur.adventofcode2019.Direction
import org.clechasseur.adventofcode2019.Pt
import org.clechasseur.adventofcode2019.Pt3D

object Day24 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val initialState = State(input.lineSequence().map { it.toList() }.toList())

    fun part1(): Int {
        val states = mutableListOf(initialState)
        while (true) {
            val newState = states.last().evolve()
            if (states.indexOf(newState) != -1) {
                return newState.biodiversity
            }
            states.add(newState)
        }
    }

    fun part2(): Int {
        val initialState3D = State3D(initialState.ground.mapIndexed { y, line ->
            line.mapIndexedNotNull { x, c -> if (c == '#') Pt3D(x, y, 0) else null }
        }.flatten().toSet())
        return generateSequence(initialState3D) { it.evolve() }.drop(200).first().bugs.size
    }

    private data class State(val ground: List<List<Char>>) {
        val biodiversity: Int
            get() = generateSequence(1) { it * 2 }.zip(ground.asSequence().flatten()).map {
                if (it.second == '#') it.first else 0
            }.sum()

        fun evolve() = State(ground.indices.map { y ->
            ground[y].indices.map { x ->
                val bugs = numBugsAround(Pt(x, y))
                when (ground[y][x]) {
                    '#' -> if (bugs == 1) '#' else '.'
                    '.' -> if (bugs in 1..2) '#' else '.'
                    else -> error("Wrong ground at ($x, $y): ${ground[y][x]}")
                }
            }
        })

        private fun numBugsAround(pt: Pt): Int = Direction.values().map { direction ->
            if (at(pt + direction.displacement) == '#') 1 else 0
        }.sum()

        private fun at(pt: Pt): Char = when {
            pt.x in ground[0].indices && pt.y in ground.indices -> ground[pt.y][pt.x]
            else -> '.'
        }
    }

    private data class State3D(val bugs: Set<Pt3D>) {
        fun evolve(): State3D {
            val newBugs = (bugs.flatMap { neighbours(it) }.toSet() - bugs).mapNotNull { pt ->
                val neighbourBugs = neighbours(pt) intersect bugs
                if (neighbourBugs.size in 1..2) pt else null
            }
            val deadBugs = bugs.mapNotNull { pt ->
                val neighbourBugs = neighbours(pt) intersect bugs
                if (neighbourBugs.size != 1) pt else null
            }
            return State3D(bugs - deadBugs + newBugs)
        }

        private fun neighbours(pt: Pt3D): Set<Pt3D> {
            val pts = Direction.values().mapNotNull { move ->
                val dest = pt + Pt3D(move.displacement.x, move.displacement.y, 0)
                if (dest.x in 0..4 && dest.y in 0..4 && dest != Pt3D(2, 2, pt.z)) dest else null
            }.toMutableSet()
            if (pt.x == 0) {
                pts.add(Pt3D(1, 2, pt.z - 1))
            } else if (pt.x == 4) {
                pts.add(Pt3D(3, 2, pt.z - 1))
            }
            if (pt.y == 0) {
                pts.add(Pt3D(2, 1, pt.z - 1))
            } else if (pt.y == 4) {
                pts.add(Pt3D(2, 3, pt.z - 1))
            }
            if (pt.x == 1 && pt.y == 2) {
                pts.addAll((0..4).map { y -> Pt3D(0, y, pt.z + 1) })
            } else if (pt.x == 3 && pt.y == 2) {
                pts.addAll((0..4).map { y -> Pt3D(4, y, pt.z + 1) })
            } else if (pt.x == 2 && pt.y == 1) {
                pts.addAll((0..4).map { x -> Pt3D(x, 0, pt.z + 1) })
            } else if (pt.x == 2 && pt.y == 3) {
                pts.addAll((0..4).map { x -> Pt3D(x, 4, pt.z + 1) })
            }
            return pts
        }
    }
}
