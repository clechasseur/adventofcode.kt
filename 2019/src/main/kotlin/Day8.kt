import org.clechasseur.adventofcode2019.math.generatePairSequence

object Day8 {
    private const val input = "<REDACTED>"
    private const val sizeX = 0 // <REDACTED>
    private const val sizeY = 0 // <REDACTED>
    private val layers = input.chunked(sizeX * sizeY).map {
        layer -> layer.chunked(sizeX).map { line -> line.map { it.toString().toInt() } }
    }

    fun part1() = layers.sortedBy { pixelCount(it, 0) }.map {
        pixelCount(it, 1) * pixelCount(it, 2)
    }.first()

    fun part2() = (0 until sizeY).map { y -> (0 until sizeX).map { x -> pixelColor(x, y) }.joinToString("") }

    private fun pixelCount(layer: List<List<Int>>, pixelValue: Int) = layer.flatten().count { it == pixelValue }

    private fun pixelColor(x: Int, y: Int) = layers.map { it[y][x] }.reduce { actual, pixel -> when (actual) {
        2 -> pixel
        else -> actual
    } }
}
