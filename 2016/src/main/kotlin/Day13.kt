import org.clechasseur.adventofcode2016.Direction
import org.clechasseur.adventofcode2016.Pt
import org.clechasseur.adventofcode2016.dij.Dijkstra
import org.clechasseur.adventofcode2016.dij.Graph
import org.clechasseur.adventofcode2016.manhattan

object Day13 {
    private const val input = 0 // <REDACTED>

    fun part1(): Int {
        val (dist, _) = Dijkstra.build(SpaceGraph(), Pt(1, 1))
        return dist[Pt(31, 39)]!!.toInt()
    }

    fun part2(): Int = Dijkstra.build(SpaceGraph(), Pt(1, 1)).dist.values.count { it <= 50 }

    private val Pt.passable: Boolean
        get() = (x * x + 3 * x + 2 * x * y + y + y * y + input).toString(2).count { it == '1' } % 2 == 0

    private class SpaceGraph : Graph<Pt> {
        override fun allPassable(): List<Pt> = (0..120).flatMap { y ->
            (0..120).map { x -> Pt(x, y) }
        }.filter { it.passable }

        override fun neighbours(node: Pt): List<Pt> = Direction.displacements.map {
            node + it
        }.filter {
            it.x >= 0 && it.y >= 0 && it.passable
        }

        override fun dist(a: Pt, b: Pt): Long = manhattan(a, b).toLong()
    }
}
