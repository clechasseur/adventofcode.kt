object Day24 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    private val parts = input.lineSequence().map { it.toPart() }.toList()

    fun part1() = buildBridges(parts, listOf(Part(0, 0))).map { it.strength() }.max()!!

    fun part2(): Int {
        val bridges = buildBridges(parts, listOf(Part(0, 0))).sortedByDescending { it.size }
        val longestBridges = bridges.filter { it.size == bridges.first().size }
        return longestBridges.map { it.strength() }.max()!!
    }

    private fun buildBridges(partsLeft: List<Part>, soFar: List<Part>): List<List<Part>> {
        val matchingParts = partsLeft.filter { part ->
            part.leftPort == soFar.last().rightPort || part.rightPort == soFar.last().rightPort
        }
        if (matchingParts.isEmpty()) {
            return listOf(soFar)
        }
        return matchingParts.flatMap { nextPart ->
            val newPartsLeft = partsLeft - nextPart
            val newSoFar = soFar + if (nextPart.leftPort == soFar.last().rightPort) {
                nextPart
            } else {
                nextPart.reverse
            }
            buildBridges(newPartsLeft, newSoFar)
        }
    }

    private data class Part(val leftPort: Int, val rightPort: Int) {
        val strength: Int
            get() = leftPort + rightPort

        val reverse: Part
            get() = Part(rightPort, leftPort)

        override fun toString(): String = "$leftPort/$rightPort"
    }

    private fun String.toPart(): Part {
        val (leftPort, rightPort) = split('/')
        return Part(leftPort.toInt(), rightPort.toInt())
    }

    private fun List<Part>.strength() = sumBy { it.strength }
}
