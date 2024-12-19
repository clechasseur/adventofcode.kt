import org.clechasseur.adventofcode2016.Pt
import org.clechasseur.adventofcode2016.move
import org.clechasseur.adventofcode2016.toDirection

object Day2 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val standardKeypad = listOf<List<Char?>>(
            listOf('1', '2', '3'),
            listOf('4', '5', '6'),
            listOf('7', '8', '9')
    )

    private val keypadByCommittee = listOf(
            listOf(null, null, '1', null, null),
            listOf(null, '2', '3', '4', null),
            listOf('5', '6', '7', '8', '9'),
            listOf(null, 'A', 'B', 'C', null),
            listOf(null, null, 'D', null, null)
    )

    fun part1() = standardKeypad.getCode()

    fun part2() = keypadByCommittee.getCode()

    private fun List<List<Char?>>.getCode(): String {
        var pos = mapIndexedNotNull { y, line ->
            val fiveX = line.mapIndexedNotNull { x, key ->
                if (key == '5') x else null
            }.firstOrNull()
            if (fiveX != null) Pt(fiveX, y) else null
        }.firstOrNull() ?: error("Cannot find 5 on keypad")

        return input.lineSequence().map { moves ->
            moves.forEach { move ->
                val possibleNewPos = pos.move(move.toDirection())
                if (isOnKeypad(possibleNewPos)) {
                    pos = possibleNewPos
                }
            }
            this[pos.y][pos.x]!!
        }.joinToString("") { it.toString() }
    }

    private fun List<List<Char?>>.isOnKeypad(pt: Pt): Boolean
            = pt.y in indices && pt.x in first().indices && this[pt.y][pt.x] != null
}
