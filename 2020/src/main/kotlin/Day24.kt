import org.clechasseur.adventofcode2020.Day24Data
import org.clechasseur.adventofcode2020.Pt

object Day24 {
    private val input = Day24Data.input

    fun part1(): Int {
        val floor = Floor()
        input.lines().map { it.toHexDirections() }.forEach { floor.followAndFlip(it) }
        return floor.blackTilesCount
    }

    fun part2(): Int {
        val floor = Floor()
        input.lines().map { it.toHexDirections() }.forEach { floor.followAndFlip(it) }
        repeat(100) { floor.sleepOnIt() }
        return floor.blackTilesCount
    }

    private val hexDirectionRegex = """[ns]?[ew]""".toRegex()

    private enum class HexDirection(val value: String, val displacement: Pt) {
        NW("nw", Pt(-1, -1)),
        NE("ne", Pt(1, -1)),
        E("e", Pt(2, 0)),
        SE("se", Pt(1, 1)),
        SW("sw", Pt(-1, 1)),
        W("w", Pt(-2, 0));

        companion object {
            fun withValue(value: String): HexDirection = values().find { it.value == value }!!
        }
    }

    private fun Pt.move(hexDirection: HexDirection): Pt = this + hexDirection.displacement

    private fun Pt.move(hexDirections: List<HexDirection>): Pt = hexDirections.fold(this) { pt, disp ->
        pt.move(disp)
    }

    private fun String.toHexDirections(): List<HexDirection> = hexDirectionRegex.findAll(this).map {
        HexDirection.withValue(it.value)
    }.toList()

    private class Floor {
        private val blackTiles = mutableSetOf<Pt>()

        val blackTilesCount: Int
            get() = blackTiles.size

        fun flip(pt: Pt) {
            if (blackTiles.contains(pt)) {
                blackTiles.remove(pt)
            } else {
                blackTiles.add(pt)
            }
        }

        fun followAndFlip(hexDirections: List<HexDirection>) {
            flip(Pt.ZERO.move(hexDirections))
        }

        fun sleepOnIt() {
            (blackTiles.filter { blackTile ->
                val neighbours = blackNeighbours(blackTile)
                neighbours == 0 || neighbours > 2
            } + whiteTiles.filter { whiteTile ->
                blackNeighbours(whiteTile) == 2
            }).forEach { tile ->
                flip(tile)
            }
        }

        private val whiteTiles: Set<Pt>
            get() = blackTiles.flatMap { blackTile ->
                HexDirection.values().map { blackTile.move(it) }
            }.filter { tile ->
                tile !in blackTiles
            }.toSet()

        private fun blackNeighbours(pt: Pt): Int = HexDirection.values().count { dir ->
            blackTiles.contains(pt.move(dir))
        }
    }
}
