import org.clechasseur.adventofcode2016.Day22Data
import org.clechasseur.adventofcode2016.Pt

object Day22 {
    private val input = Day22Data.input

    private val dfRegex = """^/dev/grid/node-x(\d+)-y(\d+)\s+(\d+)T\s+(\d+)T\s+(\d+)T\s+(\d+)%$""".toRegex()

    fun part1(): Int {
        val nodes = input.lines().associate { it.toNode() }
        return nodes.flatMap { (pta, a) ->
            nodes.filter { (ptb, _) -> ptb != pta }.map { (_, b) -> a to b }
        }.count { (a, b) ->
            a.used > 0 && a.used <= b.avail
        }
    }

    fun part2(): Int {
        // I solved this one simply by printing the maze and calculating the path by hand
//        val nodes = input.lines().associate { it.toNode() }
//        val (emptyPt, emptyNode) = nodes.asSequence().single { (_, node) -> node.used == 0 }
//        (0..37).map { x ->
//            (0..25).joinToString("") { y ->
//                val node = nodes[Pt(x, y)]!!
//                when {
//                    x == 0 && y == 0 -> "S"
//                    x == 37 && y == 0 -> "G"
//                    Pt(x, y) == emptyPt -> "_"
//                    node.size <= emptyNode.size -> "."
//                    node.used > emptyNode.size -> "#"
//                    else -> error("Wrong configuration")
//                }
//            }
//        }.forEach { println(it) }
        return 0 // <REDACTED>
    }

    private data class Node(val size: Int, val used: Int) {
        val avail: Int
            get() = size - used
    }

    private fun String.toNode(): Pair<Pt, Node> {
        val match = dfRegex.matchEntire(this) ?: error("Wrong df output: $this")
        val (x, y, size, used) = match.destructured
        return Pt(x.toInt(), y.toInt()) to Node(size.toInt(), used.toInt())
    }
}
