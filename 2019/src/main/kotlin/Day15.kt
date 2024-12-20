import org.clechasseur.adventofcode2019.Day15Data
import org.clechasseur.adventofcode2019.IntcodeComputer
import org.clechasseur.adventofcode2019.Pt
import org.clechasseur.adventofcode2019.dij.Dijkstra
import org.clechasseur.adventofcode2019.dij.Graph
import org.clechasseur.adventofcode2019.manhattan

object Day15 {
    private enum class Direction(val command: Long, val movement: Pt) {
        NORTH(1L, Pt(0, -1)),
        SOUTH(2L, Pt(0, 1)),
        WEST(3L, Pt(-1, 0)),
        EAST(4L, Pt(1, 0))
    }

    private val input = Day15Data.input

    private const val wall = 0L
    private const val moved = 1L
    private const val movedToOxygen = 2L

    fun part1(): Int {
        val computer = IntcodeComputer(input)
        val passable = mutableSetOf<Pt>()
        val oxygen = explore(computer, Pt.ZERO, passable)!!
        val pathfinding = Dijkstra.build(PassableGraph(passable), Pt.ZERO)
        return Dijkstra.assemblePath(pathfinding.prev, Pt.ZERO, oxygen)!!.size - 1
    }

    fun part2(): Int {
        val computer = IntcodeComputer(input)
        val passable = mutableSetOf<Pt>()
        val oxygen = explore(computer, Pt.ZERO, passable)!!
        val pathfinding = Dijkstra.build(PassableGraph(passable), oxygen)
        return pathfinding.dist.values.max()!!.toInt()
    }

    private fun explore(computer: IntcodeComputer, curPos: Pt, passable: MutableSet<Pt>): Pt? {
        if (passable.contains(curPos)) {
            return null
        }
        passable.add(curPos)
        return Direction.values().map { direction ->
            val snapshot = computer.snapshot()
            snapshot.addInput(direction.command)
            when (val moveResult = snapshot.readOutput()) {
                wall -> null
                moved -> explore(snapshot, curPos + direction.movement, passable)
                movedToOxygen -> {
                    val newPos = curPos + direction.movement
                    explore(snapshot, newPos, passable)
                    newPos
                }
                else -> error("Wrong movement result: $moveResult")
            }
        }.reduce { acc, pt -> acc ?: pt }
    }

    private class PassableGraph(val passable: Set<Pt>) : Graph<Pt> {
        override fun allPassable(): List<Pt> = passable.toList()

        override fun neighbours(node: Pt): List<Pt> = Direction.values().mapNotNull { direction ->
            val neighbour = node + direction.movement
            passable.find { it == neighbour }
        }.toList()

        override fun dist(a: Pt, b: Pt): Long = manhattan(a, b).toLong()
    }
}
