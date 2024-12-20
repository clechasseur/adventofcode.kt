import org.clechasseur.adventofcode2019.Pt3D
import org.clechasseur.adventofcode2019.math.leastCommonMultiple
import org.clechasseur.adventofcode2019.toPt3D
import kotlin.math.abs
import kotlin.math.sign

object Day12 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val initialMoons = input.lineSequence().map { Moon(it.toPt3D(), Pt3D.ZERO) }.toList()

    fun part1() = initialMoons.move(1_000).sumBy { it.energy }

    fun part2(): Long {
        val initialByDimension = initialMoons.byDimension()
        val periodsByDimension = mutableMapOf<Dimension, Long>()
        var moons = initialMoons
        var steps = 0L
        while (periodsByDimension.size < Dimension.values().size) {
            moons = moons.moveOnce()
            steps++
            moons.byDimension().filter { !periodsByDimension.containsKey(it.key) }.map { (dimension, state) ->
                if (state == initialByDimension[dimension]) {
                    periodsByDimension[dimension] = steps
                }
            }
        }
        return periodsByDimension.values.toList().leastCommonMultiple()
    }

    private fun List<Moon>.move(steps: Int) = generateSequence(this) { it.moveOnce() }.drop(1).take(steps).last()

    private fun List<Moon>.moveOnce() = map { moon ->
        Moon(moon.position, this.filter { it != moon }.map {
            otherMoon -> velocityChange(moon, otherMoon)
        }.fold(moon.velocity) { acc, v -> acc + v })
    }.map {
        moon -> Moon(moon.position + moon.velocity, moon.velocity)
    }

    private fun velocityChange(moon: Moon, otherMoon: Moon) = velocityChange(moon.position, otherMoon.position)

    private fun velocityChange(position: Pt3D, otherPosition: Pt3D) = Pt3D(
            (otherPosition.x - position.x).sign,
            (otherPosition.y - position.y).sign,
            (otherPosition.z - position.z).sign
    )

    private fun List<Moon>.byDimension() = Dimension.values().map {
        dimension -> dimension to map { it.forSingleDimension(dimension) }
    }.toMap()

    private fun List<Long>.leastCommonMultiple(): Long = when (size) {
        0 -> error("Need at least one element to find LCM")
        1 -> first()
        2 -> leastCommonMultiple(this[0], this[1])
        else -> (drop(2) + take(2).leastCommonMultiple()).leastCommonMultiple()
    }
}

private data class Moon(val position: Pt3D, val velocity: Pt3D) : Comparable<Moon> {
    override fun compareTo(other: Moon): Int = when (val cmp = position.compareTo(other.position)) {
        0 -> velocity.compareTo(other.velocity)
        else -> cmp
    }
}

private val Pt3D.energy get() = abs(x) + abs(y) + abs(z)

private val Moon.potentialEnergy get() = position.energy
private val Moon.kineticEnergy get() = velocity.energy
private val Moon.energy get() = potentialEnergy * kineticEnergy

private enum class Dimension {
    X, Y, Z
}

private fun Moon.forSingleDimension(dimension: Dimension): Pair<Int, Int> = when (dimension) {
    Dimension.X -> position.x to velocity.x
    Dimension.Y -> position.y to velocity.y
    Dimension.Z -> position.z to velocity.z
}
