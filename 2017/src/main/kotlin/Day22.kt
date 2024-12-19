import org.clechasseur.adventofcode2017.Direction
import org.clechasseur.adventofcode2017.Pt
import org.clechasseur.adventofcode2017.move

object Day22 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val initialInfected = input.lineSequence().zip((-12..12).asSequence()).flatMap { (line, y) ->
        line.asSequence().zip((-12..12).asSequence()).mapNotNull { (c, x) -> when (c) {
            '#' -> Pt(x, y)
            else -> null
        } }
    }.toSet()

    fun part1(): Int {
        val infected = initialInfected.toMutableSet()
        val carrier = Carrier()
        var infectedCount = 0
        (0 until 10_000).forEach { _ ->
            if (carrier.burst(infected)) {
                infectedCount++
            }
        }
        return infectedCount
    }

    fun part2(): Int {
        val nodes = initialInfected.map { it to NodeState.INFECTED }.toMap().toMutableMap()
        val carrier = SpeedierCarrier()
        var infectedCount = 0
        (0 until 10_000_000).forEach { _ ->
            if (carrier.burst(nodes)) {
                infectedCount++
            }
        }
        return infectedCount
    }

    private class Carrier {
        var pos = Pt.ZERO
        var facing = Direction.UP

        fun burst(infected: MutableSet<Pt>): Boolean {
            facing = if (infected.contains(pos)) {
                facing.right
            } else {
                facing.left
            }
            var infectedNew = false
            if (infected.contains(pos)) {
                infected.remove(pos)
            } else {
                infected.add(pos)
                infectedNew = true
            }
            pos = pos.move(facing)
            return infectedNew
        }
    }

    private enum class NodeState {
        CLEAN, WEAKENED, INFECTED, FLAGGED;

        val next: NodeState
            get() = values()[(ordinal + 1) % 4]
    }

    private class SpeedierCarrier {
        var pos = Pt.ZERO
        var facing = Direction.UP

        fun burst(nodes: MutableMap<Pt, NodeState>): Boolean {
            val nodeState = nodes[pos] ?: NodeState.CLEAN
            facing = when (nodeState) {
                NodeState.CLEAN -> facing.left
                NodeState.WEAKENED -> facing
                NodeState.INFECTED -> facing.right
                NodeState.FLAGGED -> facing.right.right
            }
            val newState = nodeState.next
            val infectedNew = newState == NodeState.INFECTED
            nodes[pos] = newState
            pos = pos.move(facing)
            return infectedNew
        }
    }
}
