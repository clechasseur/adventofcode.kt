import org.clechasseur.adventofcode2017.Pt
import org.clechasseur.adventofcode2017.manhattan
import kotlin.math.sign

object Day11 {
    private const val input = "<REDACTED>"

    private val moves = input.split(',').map { it.toSixSidedMove() }

    fun part1() = pathTo(movesSequence().last(), Pt.ZERO).size

    fun part2() = movesSequence().sortedByDescending { manhattan(it, Pt.ZERO) }.map { pathTo(it, Pt.ZERO).size }.first()

    private fun movesSequence(): Sequence<Pt> {
        val movesIt = moves.iterator()
        return generateSequence(Pt.ZERO) {
            if (movesIt.hasNext()) it + movesIt.next().displacement else null
        }
    }

    private fun pathTo(from: Pt, to: Pt): List<SixSidedMove> {
        if (from == to) {
            return emptyList()
        }
        val displacement = when (to.x) {
            from.x -> Pt(0, (to.y - from.y).sign * 2)
            else -> Pt((to.x - from.x).sign, if (to.y < from.y) -1 else 1)
        }
        val move = SixSidedMove.values().find { it.displacement == displacement } ?: error("Invalid displacement: $displacement")
        return listOf(move) + pathTo(from + displacement, to)
    }

    private enum class SixSidedMove(val code: String, val displacement: Pt) {
        NORTH_WEST("nw", Pt(-1, -1)),
        NORTH("n", Pt(0, -2)),
        NORTH_EAST("ne", Pt(1, -1)),
        SOUTH_EAST("se", Pt(1, 1)),
        SOUTH("s", Pt(0, 2)),
        SOUTH_WEST("sw", Pt(-1, 1))
    }

    private fun String.toSixSidedMove() = SixSidedMove.values().find { it.code == this } ?: error("Invalid code: $this")
}
