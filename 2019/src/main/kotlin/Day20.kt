import org.clechasseur.adventofcode2019.Direction
import org.clechasseur.adventofcode2019.Pt
import org.clechasseur.adventofcode2019.Pt3D
import org.clechasseur.adventofcode2019.dij.Dijkstra
import org.clechasseur.adventofcode2019.dij.Graph

object Day20 {
    private val modifiedInput = """
<REDACTED>
    """.trimIndent()

    private val tiles = modifiedInput.lineSequence().mapIndexed { y, line ->
        line.mapIndexed { x, c -> Pt(x, y) to c }
    }.flatten().filter { it.second != '#' && it.second != ' ' }.toMap()

    private val interesting = tiles.map { it.value to it.key }.toMap()

    private val graph = interesting.map { (_, pos) ->
        val (dist, _) = Dijkstra.build(BuilderGraph(pos), pos)
        pos to dist.filter { (pt, d) -> pt != pos && tiles[pt] != '.' && d != Long.MAX_VALUE }
    }.toMap()

    fun part1(): Long {
        val (dist, _) = Dijkstra.build(PlutoGraph(), interesting['^']!!)
        return dist[interesting['$']!!]!!
    }

    fun part2(): Long {
        val start2d = interesting['^']!!
        val end2d = interesting['$']!!
        val (dist, _) = Dijkstra.build(PlutoRecursiveGraph(), Pt3D(start2d.x, start2d.y, 0))
        return dist[Pt3D(end2d.x, end2d.y, 0)]!!
    }

    private fun Char.destPortal() = when {
        this == '0' -> '9'
        this == '9' -> '0'
        isLowerCase() -> toUpperCase()
        else -> toLowerCase()
    }

    private fun Char.destPortalMove() = when {
        this == '0' || isUpperCase() -> Pt3D(0, 0, -1)
        else -> Pt3D(0, 0, 1)
    }

    private class BuilderGraph(val start: Pt) : Graph<Pt> {
        override fun allPassable(): List<Pt> = tiles.keys.toList()

        override fun neighbours(node: Pt): List<Pt> {
            val c = tiles[node]!!
            if (c.isLetterOrDigit() && node != start) {
                return emptyList()
            }
            val dirs = Direction.displacements.mapNotNull { move ->
                val dest = node + move
                if (tiles.containsKey(dest)) dest else null
            }
            return when {
                c.isLetterOrDigit() -> dirs + interesting[tiles[node]!!.destPortal()]!!
                else -> dirs
            }
        }

        override fun dist(a: Pt, b: Pt): Long = 1L
    }

    private class PlutoGraph : Graph<Pt> {
        override fun allPassable(): List<Pt> = graph.keys.toList()

        override fun neighbours(node: Pt): List<Pt> = graph[node]!!.keys.toList()

        override fun dist(a: Pt, b: Pt): Long = graph[a]!![b]!!
    }

    private class PlutoRecursiveGraph : Graph<Pt3D> {
        companion object {
            private const val levels = 32
        }

        override fun allPassable(): List<Pt3D> = (0 until levels).flatMap { z -> graph.keys.map { Pt3D(it.x, it.y, z) } }

        override fun neighbours(node: Pt3D): List<Pt3D> {
            val node2d = Pt(node.x, node.y)
            val nodeC = tiles[node2d]!!
            return graph[node2d]!!.keys.map { dest2d ->
                val dest = Pt3D(dest2d.x, dest2d.y, node.z)
                when {
                    nodeC.isLetterOrDigit() && tiles[dest2d] == nodeC.destPortal() -> dest + nodeC.destPortalMove()
                    else -> dest
                }
            }.filter { it.z in 0 until levels }
        }

        override fun dist(a: Pt3D, b: Pt3D): Long = graph[Pt(a.x, a.y)]!![Pt(b.x, b.y)]!!
    }
}
