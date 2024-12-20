import org.clechasseur.adventofcode2020.Pt3D
import org.clechasseur.adventofcode2020.Pt4D

object Day17 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    fun part1(): Int = generateSequence(input.toSpace()) { it.nextCycle() }.drop(6).first().activeCubes.size

    fun part2(): Int = generateSequence(input.toSpace4D()) { it.nextCycle() }.drop(6).first().activeCubes.size

    private val neighbourDisplacements = (-1..1).flatMap { x ->
        (-1..1).flatMap { y ->
            (-1..1).map { z -> Pt3D(x, y, z) }
        }
    }.filter { it != Pt3D.ZERO }

    private val neighbourDisplacements4D = (-1..1).flatMap { x ->
        (-1..1).flatMap { y ->
            (-1..1).flatMap { z ->
                (-1..1).map { w -> Pt4D(x, y, z, w) }
            }
        }
    }.filter { it != Pt4D.ZERO }

    private class Space(val activeCubes: Set<Pt3D>) {
        val xIndices: IntRange
            get() = (activeCubes.minBy { it.x }?.x?.minus(1) ?: 0)..(activeCubes.maxBy { it.x }?.x?.plus(1) ?: 0)
        val yIndices: IntRange
            get() = (activeCubes.minBy { it.y }?.y?.minus(1) ?: 0)..(activeCubes.maxBy { it.y }?.y?.plus(1) ?: 0)
        val zIndices: IntRange
            get() = (activeCubes.minBy { it.z }?.z?.minus(1) ?: 0)..(activeCubes.maxBy { it.z }?.z?.plus(1) ?: 0)

        fun cubeState(pt: Pt3D): Boolean = activeCubes.contains(pt)

        fun nextCycle(): Space = Space(xIndices.flatMap { x ->
            yIndices.flatMap { y ->
                zIndices.map { z -> Pt3D(x, y, z) }
            }
        }.mapNotNull { pt ->
            val state = cubeState(pt)
            val activeNeighbours = neighbourDisplacements.map { cubeState(pt + it) }.count { it }
            if (when (state) {
                false -> activeNeighbours == 3
                true -> activeNeighbours in 2..3
            }) pt else null
        }.toSet())
    }

    private fun String.toSpace(): Space = Space(lines().withIndex().flatMap { (y, line) ->
        line.mapIndexedNotNull { x, c -> if (c == '#') Pt3D(x, y, 0) else null }
    }.toSet())

    private class Space4D(val activeCubes: Set<Pt4D>) {
        val xIndices: IntRange
            get() = (activeCubes.minBy { it.x }?.x?.minus(1) ?: 0)..(activeCubes.maxBy { it.x }?.x?.plus(1) ?: 0)
        val yIndices: IntRange
            get() = (activeCubes.minBy { it.y }?.y?.minus(1) ?: 0)..(activeCubes.maxBy { it.y }?.y?.plus(1) ?: 0)
        val zIndices: IntRange
            get() = (activeCubes.minBy { it.z }?.z?.minus(1) ?: 0)..(activeCubes.maxBy { it.z }?.z?.plus(1) ?: 0)
        val wIndices: IntRange
            get() = (activeCubes.minBy { it.w }?.w?.minus(1) ?: 0)..(activeCubes.maxBy { it.z }?.z?.plus(1) ?: 0)

        fun cubeState(pt: Pt4D): Boolean = activeCubes.contains(pt)

        fun nextCycle(): Space4D = Space4D(xIndices.flatMap { x ->
            yIndices.flatMap { y ->
                zIndices.flatMap { z ->
                    wIndices.map { w -> Pt4D(x, y, z, w) }
                }
            }
        }.mapNotNull { pt ->
            val state = cubeState(pt)
            val activeNeighbours = neighbourDisplacements4D.map { cubeState(pt + it) }.count { it }
            if (when (state) {
                false -> activeNeighbours == 3
                true -> activeNeighbours in 2..3
            }) pt else null
        }.toSet())
    }

    private fun String.toSpace4D(): Space4D = Space4D(lines().withIndex().flatMap { (y, line) ->
        line.mapIndexedNotNull { x, c -> if (c == '#') Pt4D(x, y, 0, 0) else null }
    }.toSet())
}
