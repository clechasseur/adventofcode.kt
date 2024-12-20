import org.clechasseur.adventofcode2019.Pt
import org.clechasseur.adventofcode2019.math.reduceFraction
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sign

object Day10 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val boundX = input.lineSequence().first().length
    private val boundY = input.lineSequence().count()

    private val asteroids = input.lineSequence().mapIndexed { y, line ->
        line.mapIndexed { x, c -> c to Pt(x, y) }.filter { it.first == '#' }.map { it.second }
    }.flatten().toList()

    fun part1() = asteroids.map { asteroids.seenFrom(it).size }.max()!!

    fun part2(): Int {
        val station = asteroids.maxBy { asteroids.seenFrom(it).size }!!
        val remainingAsteroids = (asteroids - station).toMutableList()
        var destroyed = 0
        while (remainingAsteroids.isNotEmpty()) {
            val targets = remainingAsteroids.seenFrom(station).sortedBy { it.angleForCannonOf(station) }
            if (targets.size + destroyed >= 200) {
                val winner = targets[199 - destroyed]
                return winner.x * 100 + winner.y
            }
            destroyed += targets.size
            remainingAsteroids.removeAll(targets)
        }
        error("Cannot find 200th asteroid")
    }

    private fun List<Pt>.seenFrom(asteroid: Pt) = this - flatMap { blockedBy(asteroid, it) } - asteroid

    private fun blockedBy(origin: Pt, blocker: Pt): List<Pt> = when (blocker) {
        origin -> emptyList()
        else -> {
            val diff = blocker - origin
            val displacement = when {
                diff.x == 0 -> Pt(0, diff.y.sign)
                diff.y == 0 -> Pt(diff.x.sign, 0)
                else -> {
                    val reduced = reduceFraction(diff.x.toLong(), diff.y.toLong())
                    Pt(reduced.first.toInt(), reduced.second.toInt())
                }
            }
            generateSequence(blocker + displacement) { it + displacement }.takeWhile { inBounds(it) }.toList()
        }
    }

    private fun inBounds(pt: Pt) = (pt.x in 0 until boundX) && (pt.y in 0 until boundY)

    private fun Pt.angleForCannonOf(station: Pt): Double {
        val diff = this - station
        val angle = atan2(diff.y.toDouble(), diff.x.toDouble())
        return when {
            angle < -PI / 2 -> 3 * PI / 2 + angle
            angle < 0.0 -> -PI / 2 + angle
            else -> angle - PI / 2
        }
    }
}
