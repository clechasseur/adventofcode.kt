import org.clechasseur.adventofcode2016.Direction
import org.clechasseur.adventofcode2016.Pt
import org.clechasseur.adventofcode2016.manhattan
import org.clechasseur.adventofcode2016.move

object Day1 {
    private const val input = "<REDACTED>"

    private val moves = input.split(", ").map { it.toMove() }

    fun part1(): Int {
        val me = Me()
        moves.forEach { me.apply(it) }
        return manhattan(me.pos, Pt.ZERO)
    }

    fun part2(): Int {
        val me = Me()
        val visited = mutableSetOf<Pt>()
        for (move in moves) {
            val pts = me.apply(move)
            pts.forEach {
                if (visited.contains(it)) {
                    return manhattan(it, Pt.ZERO)
                }
                visited.add(it)
            }
        }
        error("No location was visited twice")
    }

    private enum class TurnDirection {
        L, R
    }

    private data class Move(val turn: TurnDirection, val distance: Int)

    private fun String.toMove() = Move(TurnDirection.valueOf(this[0].toString()), substring(1).toInt())

    private class Me {
        var pos = Pt.ZERO
        var heading = Direction.UP

        fun apply(move: Move): List<Pt> {
            heading = if (move.turn == TurnDirection.L) {
                heading.left
            } else {
                heading.right
            }
            return (0 until move.distance).map { _ ->
                pos = pos.move(heading)
                pos
            }
        }
    }
}
