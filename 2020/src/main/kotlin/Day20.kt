import org.clechasseur.adventofcode2020.Day20Data
import org.clechasseur.adventofcode2020.Direction
import org.clechasseur.adventofcode2020.Pt
import org.clechasseur.adventofcode2020.move

object Day20 {
    private val input = Day20Data.input

    private val idRegex = """^Tile (\d+):$""".toRegex()

    private val seaMonster = """
                          # 
        #    ##    ##    ###
         #  #  #  #  #  #   
    """.trimIndent().lines()

    fun part1(): Long {
        val tiles = input.toTiles()
        val corners = tiles.map { tile ->
            tile to Direction.values().mapNotNull { direction ->
                tiles.asSequence().filter {
                    it != tile
                }.flatMap {
                    it.orientationSequence()
                }.firstOrNull {
                    tile.canConnect(it, direction)
                }
            }.count()
        }.filter { (_, neighbours) ->
            neighbours == 2
        }.map { (tile, _) ->
            tile
        }
        require(corners.size == 4) { "There's ${corners.size} corners?!?" }
        return corners.fold(1L) { acc, tile -> acc * tile.id }
    }

    fun part2(): Int {
        val tiles = input.toTiles()
        val firstTile = tiles.first()
        val quilt = buildQuilt(Quilt(firstTile), tiles - firstTile) ?: error("Could not build quilt")
        require(quilt.square) { "Final quilt is not a square" }
        val bigTile = quilt.cropAll().toTile(id = 0L)
        val numMonsters = countSeaMonsters(bigTile)
        return bigTile.lines.sumBy { line ->
            line.count { it == '#' }
        } - seaMonster.sumBy { line ->
            line.count { it == '#' }
        } * numMonsters
    }

    private fun buildQuilt(quilt: Quilt, tiles: Set<Tile>): Quilt? = if (tiles.isEmpty()) {
        quilt
    } else {
        sequence {
            tiles.asSequence().forEach { tile ->
                quilt.edges.asSequence().forEach { edgePt ->
                    tile.orientationSequence().mapNotNull { orientedTile ->
                        val sewnQuilt = quilt.trySew(edgePt, orientedTile)
                        if (sewnQuilt != null) buildQuilt(sewnQuilt, tiles - tile) else null
                    }.forEach { builtQuilt ->
                        yield(builtQuilt)
                    }
                }
            }
        }.firstOrNull()
    }

    private fun countSeaMonsters(bigTile: Tile): Int = bigTile.orientationSequence().map { tile ->
        (tile.lines.indices.first..tile.lines.indices.last - seaMonster.size + 1).flatMap { y ->
            (tile.lines.first().indices.first..tile.lines.first().indices.last - seaMonster.first().length + 1).map { x ->
                val picture = tile.lines.subList(y, y + seaMonster.size).map { line ->
                    line.substring(x, x + seaMonster.first().length)
                }
                picture.zip(seaMonster).all { (fromPic, fromMonster) ->
                    fromPic.zip(fromMonster).all { (cFromPic, cFromMonster) ->
                        cFromMonster == ' ' || cFromPic == cFromMonster
                    }
                }
            }
        }.count { it }
    }.max()!!

    private data class Tile(val id: Long, val data: List<String>) {
        val lines: List<String>
            get() = data
        val columns: List<String> by lazy {
            data.indices.map { x -> data.joinToString("") { it[x].toString() } }
        }

        fun flipVertically(): Tile = copy(data = lines.reversed())
        fun rotateClockwise(): Tile = copy(data = lines.indices.map { x ->
            lines.indices.reversed().joinToString("") { y -> data[y][x].toString() }
        })

        fun orientationSequence(): Sequence<Tile> = generateSequence(this) { tile ->
            tile.rotateClockwise()
        }.take(4).flatMap { tile ->
            sequenceOf(tile, tile.flipVertically())
        }

        fun canConnect(tile: Tile, direction: Direction): Boolean = when (direction) {
            Direction.UP -> lines.first() == tile.lines.last()
            Direction.DOWN -> lines.last() == tile.lines.first()
            Direction.LEFT -> columns.first() == tile.columns.last()
            Direction.RIGHT -> columns.last() == tile.columns.first()
        }

        fun crop(): Tile = copy(data = lines.drop(1).dropLast(1).map {
            it.substring(1 until it.length - 1)
        })

        override fun toString(): String = lines.joinToString("\n")
    }

    private data class Quilt(val tiles: Map<Pt, Tile>) {
        constructor(firstTile: Tile) : this(mapOf(Pt.ZERO to firstTile))

        val edges: List<Pt>
            get() = tiles.keys.flatMap { pt ->
                Direction.values().map { pt.move(it) }
            }.filter {
                it !in tiles
            }

        val xIndices: IntRange
            get() = tiles.keys.minBy { it.x }!!.x..tiles.keys.maxBy { it.x }!!.x
        val yIndices: IntRange
            get() = tiles.keys.minBy { it.y }!!.y..tiles.keys.maxBy { it.y }!!.y
        val square: Boolean
            get() = (xIndices.last - xIndices.first) == (yIndices.last - yIndices.first)

        fun trySew(pt: Pt, tile: Tile): Quilt? {
            val fits = Direction.values().map { direction ->
                pt.move(direction) to direction
            }.filter { (existingPt, _) ->
                existingPt in tiles
            }.all { (existingPt, direction) ->
                tile.canConnect(tiles[existingPt]!!, direction)
            }
            return if (fits) Quilt(tiles + (pt to tile)) else null
        }

        fun cropAll(): Quilt = Quilt(tiles.mapValues { (_, tile) -> tile.crop() })

        fun toTile(id: Long): Tile = Tile(id, yIndices.flatMap { y ->
            val tile = tiles[Pt(xIndices.first, y)]!!
            tile.lines.indices.map { tileY ->
                xIndices.joinToString("") { x ->
                    tiles[Pt(x, y)]!!.lines[tileY]
                }
            }
        })
    }

    private fun String.toTiles(): Set<Tile> {
        val tiles = mutableSetOf<Tile>()
        val toParse = lines()
        var i = 0
        while (i < toParse.size) {
            val match = idRegex.matchEntire(toParse[i]) ?: error("Wrong tile ID: ${toParse[i]}")
            val id = match.groupValues[1].toLong()
            var j = i + 1
            while (j < toParse.size && toParse[j].isNotEmpty()) {
                j++
            }
            val data = toParse.subList(i + 1, j)
            tiles.add(Tile(id, data))
            i = j + 1
        }
        return tiles
    }
}
