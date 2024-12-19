import org.clechasseur.adventofcode2016.Day24Data
import org.clechasseur.adventofcode2016.Direction
import org.clechasseur.adventofcode2016.Pt
import org.clechasseur.adventofcode2016.dij.Dijkstra
import org.clechasseur.adventofcode2016.dij.Graph
import org.clechasseur.adventofcode2016.math.permutations
import kotlin.math.min

object Day24 {
    private val input = Day24Data.input

    fun part1(): Int {
        val maze = input.toMaze()
        val paths = permutations(maze.toVisit.values.toList())
        var shortest = Int.MAX_VALUE
        pathLoop@ for (path in paths) {
            var visits = path
            var pathMaze = maze
            var pos = maze.startPos
            var steps = 0
            while (pathMaze.toVisit.isNotEmpty()) {
                if (steps > shortest) {
                    continue@pathLoop
                }
                val (dist, _) = Dijkstra.build(pathMaze, pos)
                val next = pathMaze.toVisit.filterValues { it == visits.last() }.keys.single()
                steps += dist[next]!!.toInt()
                pos = next
                pathMaze = pathMaze.afterVisit(next)
                visits = visits.dropLast(1)
            }
            shortest = min(steps, shortest)
        }
        return shortest
    }

    fun part2(): Int {
        val maze = input.toMaze()
        val paths = permutations(maze.toVisit.values.toList())
        var shortest = Int.MAX_VALUE
        pathLoop@ for (path in paths) {
            var visits = listOf('0') + path
            var pathMaze = maze
            var pos = maze.startPos
            var steps = 0
            while (visits.isNotEmpty()) {
                if (steps > shortest) {
                    continue@pathLoop
                }
                val (dist, _) = Dijkstra.build(pathMaze, pos)
                val next = if (visits.last() != '0') {
                    pathMaze.toVisit.filterValues { it == visits.last() }.keys.single()
                } else {
                    pathMaze.startPos
                }
                steps += dist[next]!!.toInt()
                pos = next
                pathMaze = if (visits.last() != '0') {
                    pathMaze.afterVisit(next)
                } else {
                    pathMaze
                }
                visits = visits.dropLast(1)
            }
            shortest = min(steps, shortest)
        }
        return shortest
    }
    
    private class Maze(val tiles: Map<Pt, Char>) : Graph<Pt> {
        val startPos = tiles.filterValues { it == '0' }.keys.single()
        val toVisit = tiles.filterValues { it.toString().toIntOrNull() != null && it != '0' }

        override fun allPassable(): List<Pt> = tiles.keys.toList()
        override fun neighbours(node: Pt): List<Pt> = Direction.displacements.mapNotNull {
            val pt = node + it
            if (tiles.containsKey(pt)) pt else null
        }
        override fun dist(a: Pt, b: Pt): Long = 1L

        fun afterVisit(pt: Pt): Maze = Maze(tiles.filterKeys { it != pt } + mapOf(pt to '.'))
    }
    
    private fun String.toMaze(): Maze = Maze(lineSequence().withIndex().flatMap { (x, line) ->
        line.mapIndexed { y, c -> if (c != '#') Pt(x, y) to c else null }.asSequence()
    }.filterNotNull().toMap())
}
