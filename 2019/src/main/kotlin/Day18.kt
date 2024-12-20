import org.clechasseur.adventofcode2019.Pt
import org.clechasseur.adventofcode2019.dij.Dijkstra
import org.clechasseur.adventofcode2019.dij.Graph

object Day18 {
    private fun inputTemplate(one: String, two: String, three: String) = """
        <REDACTED>
    """.trimIndent()

    private val input1 = inputTemplate("", "", "") // <REDACTED>
    private val input2 = inputTemplate("", "", "") // <REDACTED>

    fun part1(): Long = getMinPath(input1).steps

    fun part2(): Long = getMinPath(input2).steps

    private fun getMinPath(input: String): KeyPath {
        val labyrinth = input.lineSequence().mapIndexed {
            y, line -> line.mapIndexed { x, c -> Pt(x, y) to c }
        }.flatten().toMap()
        val interesting = labyrinth.filter { it.value == '@' || it.value.isLetter() }
        val graph = mutableMapOf<Pt, MutableMap<Pt, Long>>()
        interesting.map { (pos, c) ->
            val (dist, _) = Dijkstra.build(LabyrinthGraph(labyrinth, pos), pos)
            interesting.filter { it.value != c }.forEach { (subPos, _) ->
                val subDist = dist[subPos]
                if (subDist != null && subDist != Long.MAX_VALUE) {
                    graph.getOrPut(pos) { mutableMapOf() }[subPos] = subDist
                }
            }
        }
        val robots = interesting.filter { it.value == '@' }.keys.toList()

        var paths = setOf(KeyPath(robots, emptyList(), 0L))
        while (paths.map { it.keys.size }.max()!! < 26) {
            paths = forward(paths, interesting, graph)
        }
        return paths.minBy { it.steps }!!
    }

    private fun forward(paths: Set<KeyPath>, interesting: Map<Pt, Char>, graph: Map<Pt, Map<Pt, Long>>): Set<KeyPath> {
        return paths.flatMap { path ->
            path.robots.flatMap { robotPos ->
                val nextKeys = mutableMapOf<Pt, Long>()
                getNextKeys(robotPos, path, interesting, graph, nextKeys)
                nextKeys.filterNot { interesting[it.key]!!.isUpperCase() }.map { (keyPos, dist) ->
                    val robotIdx = path.robots.indexOf(robotPos)
                    val newRobots = path.robots.toMutableList()
                    newRobots[robotIdx] = keyPos
                    KeyPath(newRobots, path.keys + interesting[keyPos]!!, path.steps + dist)
                }
            }
        }.fold(mutableMapOf<KeyPath, Long>()) { acc, path ->
            val existingSteps = acc[path]
            if (existingSteps == null || path.steps < existingSteps) {
                acc.remove(path)
                acc[path] = path.steps
            }
            acc
        }.keys
    }

    private fun getNextKeys(from: Pt, path: KeyPath, interesting: Map<Pt, Char>, graph: Map<Pt, Map<Pt, Long>>,
                            keys: MutableMap<Pt, Long>, distSoFar: Long = 0) {

        graph[from]?.forEach { (pos, dist) ->
            val feature = interesting[pos] ?: error("Unknown pos")
            if (feature.isLetter() && !path.keysSet.contains(feature)) {
                val existingDist = keys[pos]
                if (existingDist == null || dist + distSoFar < existingDist) {
                    keys.remove(pos)
                    keys[pos] = dist + distSoFar
                    if (feature.isUpperCase() && path.keysSet.contains(feature.toLowerCase())) {
                        getNextKeys(pos, path, interesting, graph, keys, dist + distSoFar)
                    }
                }
            }
        }
    }

    private class LabyrinthGraph(private val labyrinth: Map<Pt, Char>, private val start: Pt) : Graph<Pt> {
        companion object {
            private val directions = listOf(Pt(1, 0), Pt(-1, 0), Pt(0, 1), Pt(0, -1))
        }

        private val passable = labyrinth.asSequence().filter {
            it.value == '@' || it.value == '.' || it.value.isLetter()
        }.map { it.key }.toList()

        override fun allPassable(): List<Pt> = passable

        override fun neighbours(node: Pt): List<Pt> = directions.map { it + node }.filter {
            val srcType = labyrinth[node] ?: error("Source node must be known")
            val destType = labyrinth[it]
            (!srcType.isUpperCase() || node == start) && destType != null && destType != '#'
        }

        override fun dist(a: Pt, b: Pt): Long = 1L
    }

    private class KeyPath(val robots: List<Pt>, val keys: List<Char>, val steps: Long) {
        val keysSet = keys.toSet()

        override fun equals(other: Any?): Boolean
                = other is KeyPath && other.keysSet == keysSet && other.robots == robots

        override fun hashCode(): Int {
            var result = keysSet.hashCode()
            result = 31 * result + robots.hashCode()
            return result
        }

        override fun toString(): String = "$steps steps: ${keys.joinToString()}"
    }
}
