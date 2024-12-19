import kotlin.math.sqrt

object Day21 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val pattern2Regex = Regex("^(.)(.)/(.)(.)$")
    private const val pattern2Replacement = "$3$1/$4$2"

    private val pattern3Regex = Regex("^(.)(.)(.)/(.)(.)(.)/(.)(.)(.)$")
    private const val pattern3Replacement = "$7$4$1/$8$5$2/$9$6$3"

    private const val initialCanvas = "<REDACTED>"

    fun part1() = iterations(buildRules()).drop(5).first().count { it == '#' }

    fun part2() = iterations(buildRules()).drop(18).first().count { it == '#' }

    private fun iterations(rules: Map<String, String>) = generateSequence(initialCanvas) { canvas ->
        assemble(divide(canvas).map { rules[it] ?: error("No rule found for $it") })
    }

    private fun buildRules(): Map<String, String> = input.lineSequence().flatMap { line ->
        val (inputPattern, outputPattern) = line.split(" => ")
        flip(inputPattern).flatMap { rotate(it) }.map { it to outputPattern }.asSequence()
    }.toMap()

    private fun rotate(pattern: String): List<String> = generateSequence(pattern) { prev -> when (prev.length) {
        5 -> pattern2Regex.replace(prev, pattern2Replacement)
        11 -> pattern3Regex.replace(prev, pattern3Replacement)
        else -> error("Wrong pattern format: $pattern")
    } }.take(4).toList()

    private fun flip(pattern: String): List<String> {
        val flippedVertically = pattern.split('/').reversed().joinToString("/")
        return listOf(pattern, flippedVertically).flatMap { pat ->
            val flippedHorizontally = pat.split('/').joinToString("/") { it.reversed() }
            listOf(pat, flippedHorizontally)
        }
    }

    private fun divide(canvas: String): List<String> {
        val canvasLines = canvas.split('/')
        val divisor = if (canvasLines.size % 2 == 0) 2 else 3
        return canvasLines.chunked(divisor).flatMap { lines ->
            (lines[0].indices step divisor).map { firstIdx ->
                (0 until divisor).joinToString("/") { lines[it].substring(firstIdx, firstIdx + divisor) }
            }
        }
    }

    private fun assemble(chunks: List<String>): String {
        val chunksPerLine = sqrt(chunks.size.toDouble()).toInt()
        return chunks.chunked(chunksPerLine).joinToString("/") { lineChunks ->
            val splitLineChunks = lineChunks.map { it.split('/') }
            splitLineChunks[0].indices.joinToString("/") { lineIdx ->
                splitLineChunks.joinToString("") { it[lineIdx] }
            }
        }
    }
}
