object Day13 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val inputRegex = Regex("""^(\d+): (\d+)$""")

    fun part1() = tripSeverity(0).first

    fun part2() = generateSequence(0) { it + 1 }.map { tripSeverity(it) }.takeWhile { it.second }.count()

    private fun tripSeverity(delay: Int): Pair<Int, Boolean> {
        val layers = input.lineSequence().map { it.toLayer() }.map { it.depth to it }.toMap()
        val lastLayer = layers.keys.max()!!
        layers.values.forEach { it.picomove(delay) }
        var packet = 0
        var severity = 0
        var caught = false
        while (packet <= lastLayer) {
            val layer = layers[packet]
            if (layer != null && layer.position == 0) {
                severity += layer.severity
                caught = true
            }
            layers.forEach { (_, l) -> l.picomove(1) }
            packet++
        }
        return severity to caught
    }

    private class Layer(val depth: Int, val range: Int) {
        private var offset = 1

        var position = 0
            private set

        val severity: Int
            get() = depth * range

        fun picomove(by: Int) {
            (0 until (by % (range * 2 - 2))).forEach { _ ->
                position += offset
                if (position == 0 || position == (range - 1)) {
                    offset = -offset
                }
            }
        }

        override fun toString() = "{$depth} [$range] -> $position"
    }

    private fun String.toLayer(): Layer {
        val match = inputRegex.matchEntire(this) ?: error("Wrong layer format: $this")
        return Layer(match.groupValues[1].toInt(), match.groupValues[2].toInt())
    }
}
