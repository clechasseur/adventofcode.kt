import org.clechasseur.adventofcode2019.IntcodeComputer
import org.clechasseur.adventofcode2019.Pt
import org.clechasseur.adventofcode2019.manhattan
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.round

object Day19 {
    private val input = listOf<Long>() // <REDACTED>

    fun part1() = (0 until 50).flatMap { y ->
        (0 until 50).map { x -> isInBeam(Pt(x, y)) }
    }.count { it }

    fun part2(): Int {
        val candidates = beam().map {
            it to biggestSquare(it)
        }.dropWhile {
            it.second < 100
        }.takeWhile {
            it.second == 100
        }.sortedBy { manhattan(Pt.ZERO, it.first) }.toList()
        val best = candidates.first().first
        return best.x * 10_000 + best.y
    }

    private fun isInBeam(pt: Pt) = IntcodeComputer(input, pt.x.toLong(), pt.y.toLong()).readOutput() == 1L

    private fun beam(): Sequence<Pt> = sequence {
        var start = Pt(6, 5)
        while (true) {
            var pt = start
            while (isInBeam(pt)) {
                yield(pt)
                pt = Pt(pt.x + 1, pt.y)
            }

            start = Pt(start.x, start.y + 1)
            while (!isInBeam(start)) {
                start = Pt(start.x + 1, start.y)
            }
        }
    }

    private fun biggestSquare(topLeft: Pt): Int = min(verticalLineLength(topLeft), horizontalLineLength(topLeft))

    private fun verticalLineLength(pt: Pt): Int = lineLength(pt, Pt(0, 1))

    private fun horizontalLineLength(pt: Pt): Int = lineLength(pt, Pt(1, 0))

    private fun lineLength(pt: Pt, move: Pt): Int
            = generateSequence(pt) { it + move }.map { isInBeam(it) }.takeWhile { it }.count()
}
