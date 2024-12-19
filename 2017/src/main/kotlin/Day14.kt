import org.clechasseur.adventofcode2017.Direction
import org.clechasseur.adventofcode2017.KnotHash
import org.clechasseur.adventofcode2017.Pt

object Day14 {
    private const val input = "<REDACTED>"

    private val ones: Set<Pt> by lazy {
        (0 until 128).flatMap { y ->
            KnotHash("$input-$y").toString().chunked(8).map {
                it.toLong(16)
            }.joinToString("") {
                it.toString(2).padStart(32, '0')
            }.mapIndexed { x, bit ->
                Pt(x, y) to bit
            }.filter {
                it.second == '1'
            }.map { it.first }
        }.toSet()
    }

    private val regions: List<Set<Pt>> by lazy {
        val regions = mutableListOf<Set<Pt>>()
        val seen = mutableSetOf<Pt>()
        var next = ones.firstOrNull { !seen.contains(it) }
        while (next != null) {
            val region = explore(next)
            regions.add(region)
            seen.addAll(region)
            next = ones.firstOrNull { !seen.contains(it) }
        }
        regions
    }

    fun part1() = ones.count()

    fun part2() = regions.count()

    private fun explore(from: Pt): Set<Pt> = mutableSetOf<Pt>().apply {
        exploreIn(from, this)
    }

    private fun exploreIn(pt: Pt, region: MutableSet<Pt>) {
        if (!region.contains(pt)) {
            region.add(pt)
            Direction.displacements.filter {
                ones.contains(pt + it)
            }.forEach {
                exploreIn(pt + it, region)
            }
        }
    }
}
