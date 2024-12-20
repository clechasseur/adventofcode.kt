import kotlin.math.abs
import kotlin.math.min

object Day16 {
    private val input = """
        <REDACTED>
    """.trimIndent().asSequence().map { it.toString().toLong() }.toList()

    private val basePattern = listOf<Long>() // <REDACTED>

    fun part1() = fft(input).take(100).last().take(8).joinToString("").toInt()

    fun part2(): Int {
        val bigInput = input.asSequence().keepGoing().take(10_000 * input.size).toList()
        val offset = bigInput.take(7).joinToString("").toInt()
        val result = tailFft(bigInput, offset).take(100).last()
        return result.drop(offset).take(8).joinToString("").toInt()
    }

    private fun fft(signal: List<Long>): Sequence<List<Long>> = generateSequence(signal) {
        prevSignal -> signal.indices.map { elementIdx ->
            var idx = elementIdx
            var result = 0L
            var sign = 1
            while (idx < prevSignal.size) {
                val readOffset = min(elementIdx, prevSignal.size - (idx + 1))
                if (sign > 0) {
                    for (valIdx in idx..(idx + readOffset)) {
                        result += prevSignal[valIdx]
                    }
                } else {
                    for (valIdx in idx..(idx + readOffset)) {
                        result -= prevSignal[valIdx]
                    }
                }
                sign = -sign
                idx += (elementIdx + 1) * 2
            }
            abs(result) % 10
        }
    }.drop(1)

    private fun tailFft(signal: List<Long>, offset: Int): Sequence<List<Long>> = generateSequence(signal) { prevSignal ->
        val nextSignal = MutableList(prevSignal.size) { 0L }
        nextSignal[nextSignal.size - 1] = prevSignal.last()
        for (i in (prevSignal.indices.last - 1) downTo offset) {
            nextSignal[i] = prevSignal[i] + nextSignal[i + 1]
        }
        for (i in (prevSignal.indices.last - 1) downTo offset) {
            nextSignal[i] = nextSignal[i] % 10
        }
        nextSignal
    }.drop(1)
}

private fun <T> Sequence<T>.keepGoing() = sequence {
    val values = toList()
    if (values.isNotEmpty()) {
        while (true) {
            yieldAll(values)
        }
    }
}
