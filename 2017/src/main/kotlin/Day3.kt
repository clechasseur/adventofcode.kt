import org.clechasseur.adventofcode2017.Direction
import org.clechasseur.adventofcode2017.Pt
import org.clechasseur.adventofcode2017.manhattan

object Day3 {
    private const val input = 0 // <REDACTED>

    private val allAround = listOf(
        Pt(-1, -1), Pt(-1, 0), Pt(-1, 1),
        Pt(0, -1), Pt(0, 1),
        Pt(1, -1), Pt(1, 0), Pt(1, 1)
    )

    fun part1() = manhattan(spiral().drop(input - 1).first(), Pt.ZERO)

    fun part2() = spiralTestValues().dropWhile { it < input }.first()

    private fun spiral(): Sequence<Pt> = sequence {
        var pt = Pt.ZERO
        var dir = Direction.RIGHT
        var moves = 1
        yield(pt)
        while (true) {
            (0 until moves).forEach { _ ->
                pt += dir.displacement
                yield(pt)
            }
            dir = dir.right
            (0 until moves).forEach { _ ->
                pt += dir.displacement
                yield(pt)
            }
            dir = dir.right
            moves++
        }
    }

    private fun spiralTestValues(): Sequence<Int> = sequence {
        val spir = spiral().iterator()
        val map = mutableMapOf<Pt, Int>()
        map[spir.next()] = 1
        while (true) {
            val pt = spir.next()
            val value = allAround.mapNotNull { map[pt + it] }.sum()
            yield(value)
            map[pt] = value
        }
    }
}
