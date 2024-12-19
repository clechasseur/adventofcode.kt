import org.clechasseur.adventofcode2017.Pt3D
import org.clechasseur.adventofcode2017.manhattan

object Day20 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val particleRegex = Regex("""p=<(-?\d+),(-?\d+),(-?\d+)>, v=<(-?\d+),(-?\d+),(-?\d+)>, a=<(-?\d+),(-?\d+),(-?\d+)>""")

    private val particles = input.lineSequence().map { it.toParticle() }.toList()

    fun part1() = particles.withIndex().minBy { manhattan(Pt3D.ZERO, it.value.acceleration) }!!.index

    fun part2(): Int {
        var remainingParticles = particles.mapIndexed { index, particle -> index to particle }
        var distBetween = computeDistBetween(remainingParticles)
        while (true) {
            val collisions = distBetween.filterValues { it == 0 }.keys.flatMap { listOf(it.first, it.second) }
            val withoutCollided = remainingParticles.filter { !collisions.contains(it.first) }
            val moved = withoutCollided.map { it.first to it.second.move() }
            val distBetweenMoved = computeDistBetween(moved)
            remainingParticles = moved
            if (allGoingAway(distBetween, distBetweenMoved)) {
                break
            }
            distBetween = distBetweenMoved
        }
        return remainingParticles.size
    }

    private fun computeDistBetween(remainingParticles: List<Pair<Int, Particle>>): Map<Pair<Int, Int>, Int> {
        val distBetween = mutableMapOf<Pair<Int, Int>, Int>()
        remainingParticles.forEach { (fromIdx, fromParticle) ->
            remainingParticles.filter { it.first != fromIdx }.forEach { (toIdx, toParticle) ->
                if (!distBetween.contains(fromIdx to toIdx)) {
                    val dist = manhattan(fromParticle.position, toParticle.position)
                    distBetween[fromIdx to toIdx] = dist
                    distBetween[toIdx to fromIdx] = dist
                }
            }
        }
        return distBetween
    }

    private fun allGoingAway(distBetween0: Map<Pair<Int, Int>, Int>, distBetween1: Map<Pair<Int, Int>, Int>): Boolean {
        return distBetween0.all { (fromTo, dist0) ->
            val dist1 = distBetween1[fromTo]
            dist1 == null || dist1 > dist0
        }
    }

    private data class Particle(val position: Pt3D, val speed: Pt3D, val acceleration: Pt3D)

    private fun String.toParticle(): Particle {
        val match = particleRegex.matchEntire(this) ?: error("Wrong particle format: $this")
        return Particle(
                Pt3D(match.groupValues[1].toInt(), match.groupValues[2].toInt(), match.groupValues[3].toInt()),
                Pt3D(match.groupValues[4].toInt(), match.groupValues[5].toInt(), match.groupValues[6].toInt()),
                Pt3D(match.groupValues[7].toInt(), match.groupValues[8].toInt(), match.groupValues[9].toInt())
        )
    }

    private fun Particle.move(): Particle {
        val newSpeed = speed + acceleration
        val newPosition = position + newSpeed
        return Particle(newPosition, newSpeed, acceleration)
    }
}
