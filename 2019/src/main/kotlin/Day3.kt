import org.clechasseur.adventofcode2019.Direction
import org.clechasseur.adventofcode2019.Pt
import org.clechasseur.adventofcode2019.manhattan
import org.clechasseur.adventofcode2019.toDirection

object Day3 {
    private const val input = "<REDACTED>"
    private val moves = input.split('\n').map { wire ->
        wire.splitToSequence(',').map { it.toMove() }.toList()
    }

    fun part1(): Int {
        val (wire1Pts, wire2Pts) = moves.map { wirePoints(it) }
        return manhattan(wire1Pts.intersect(wire2Pts).min()!!, Pt(0, 0))
    }

    fun part2(): Int {
        val ptsToDists = mutableMapOf<Pt, MutableList<Int>>()
        moves.flatMap {
            move -> wirePoints(move).mapIndexed { i, pt -> pt to i + 1 }.distinctBy { it.first }
        }.forEach {
            ptsToDists.getOrPut(it.first) { mutableListOf() }.add(it.second)
        }
        return ptsToDists.filter { it.value.size > 1 }.map { it.value.sum() }.min()!!
    }

    private fun wirePoints(moves: List<Move>): List<Pt> {
        val movesIt = moves.iterator()
        return generateSequence(listOf(Pt(0, 0))) { prev -> when (movesIt.hasNext()) {
            true -> {
                val move = movesIt.next()
                generateSequence(prev.last()) {
                    it + move.direction.displacement
                }.drop(1).take(move.distance).toList()
            }
            false -> null
        } }.drop(1).flatten().toList()
    }
}

private data class Move(val direction: Direction, val distance: Int)

private fun String.toMove() = Move(this[0].toDirection(), substring(1).toInt())
