import org.clechasseur.adventofcode2016.Direction
import org.clechasseur.adventofcode2016.Pt
import org.clechasseur.adventofcode2016.md5

object Day17 {
    private const val input = "<REDACTED>"

    fun part1(): String = shortestPath(State(Pt.ZERO, emptyList()))

    fun part2(): Int = longestPath(State(Pt.ZERO, emptyList())).length

    private fun shortestPath(initialState: State): String = generateSequence(listOf(initialState)) { ls ->
        ls.flatMap { it.possibilities() }.apply {
            require(isNotEmpty()) { "No path found" }
        }
    }.dropWhile { ls ->
        ls.none { it.inVault }
    }.first().first { it.inVault }.path.toPathString()

    private fun longestPath(initialState: State): String {
        var states = listOf(initialState)
        var longestState: State? = null
        while (states.isNotEmpty()) {
            states = states.flatMap { it.possibilities() }
            states.firstOrNull { it.inVault }?.apply { longestState = this }
            states = states.filterNot { it.inVault }
        }
        return longestState?.path?.toPathString() ?: error("No path found")
    }

    private data class State(val pos: Pt, val path: List<Direction>) {
        val inVault: Boolean
            get() = pos == Pt(3, 3)

        fun possibilities(): List<State> = Direction.values().filter { d ->
            val next = pos + d.displacement
            next.x in 0..3 && next.y in 0..3
        }.filter { d ->
            val hash = md5("$input${path.toPathString()}")
            hash[d.hashIndex].open
        }.map {
            State(pos + it.displacement, path + it)
        }
    }

    private fun List<Direction>.toPathString(): String = joinToString("") { it.c.toString() }

    private val Direction.hashIndex: Int
        get() = when (this) {
            Direction.UP -> 0
            Direction.DOWN -> 1
            Direction.LEFT -> 2
            Direction.RIGHT -> 3
        }

    private val Char.open: Boolean
        get() = this in 'b'..'f'
}
