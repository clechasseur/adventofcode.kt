import org.clechasseur.adventofcode2017.Direction
import org.clechasseur.adventofcode2017.Pt
import org.clechasseur.adventofcode2017.move

object Day19 {
    private val input = """
<REDACTED>
""".trimIndent().lineSequence().map { it.toList() }.toList()

    fun part1() = followPath().filter { it.isLetter() }.joinToString("")

    fun part2() = followPath().size

    private fun at(pt: Pt): Char {
        if (pt.y !in input.indices || pt.x !in input[0].indices) {
            return ' '
        }
        return input[pt.y][pt.x]
    }

    private fun followPath(): List<Char> {
        var pos = Pt(input[0].indexOf('|'), 0)
        var dir = Direction.DOWN
        val letters = mutableListOf<Char>()
        while (at(pos) != ' ') {
            letters.add(at(pos))
            if (at(pos) == '+') {
                dir = if (at(pos.move(dir.left)) != ' ') dir.left else dir.right
            }
            pos = pos.move(dir)
        }
        return letters
    }
}
