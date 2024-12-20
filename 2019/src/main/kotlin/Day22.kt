import kotlin.math.max

object Day22 {
    private val input = """
        <REDACTED>
    """.trimIndent()

    // Stole this one from: https://github.com/nibarius/aoc/blob/master/src/main/aoc2019/Day22.kt

    fun part1(): Long {
        val deckSize = 10_007L
        val techniques = reduce(input.lineSequence().toList().flatMap { it.toTechniques(deckSize) }, deckSize)
        return finalPositionForCard(card = 2019L, techniques = techniques, deckSize = deckSize)
    }

    fun part2(): Long {
        val deckSize = 119_315_717_514_047L
        val repeats = 101_741_582_076_661L
        val targetPosition = 2020L

        val shufflesLeftUntilInitialState = deckSize - 1 - repeats

        val techniques = reduce(input.lineSequence().toList().flatMap { it.toTechniques(deckSize) }, deckSize)
        val reduced = reduce(techniques, deckSize)
        val repeated = repeat(reduced, shufflesLeftUntilInitialState, deckSize)

        return finalPositionForCard(card = targetPosition, techniques = repeated, deckSize = deckSize)
    }
}

private interface Technique {
    fun nextPositionForCard(card: Long, deckSize: Long): Long
    fun canBeCombinedWith(other: Technique): Boolean
    fun combine(other: Technique, deckSize: Long): List<Technique>
}

private class Cut(val n: Long) : Technique {
    override fun nextPositionForCard(card: Long, deckSize: Long): Long = Math.floorMod(card - n, deckSize)

    override fun canBeCombinedWith(other: Technique): Boolean = true

    override fun combine(other: Technique, deckSize: Long): List<Technique> = when (other) {
        is Cut -> listOf(Cut(Math.floorMod(n + other.n, deckSize)))
        is DealWithIncrement -> listOf(DealWithIncrement(other.n), Cut(mulMod(n, other.n, deckSize)))
        else -> error("Invalid technique combination")
    }
}

private class DealWithIncrement(val n: Long): Technique {
    override fun nextPositionForCard(card: Long, deckSize: Long): Long = Math.floorMod(card * n, deckSize)

    override fun canBeCombinedWith(other: Technique): Boolean = other !is Cut

    override fun combine(other: Technique, deckSize: Long): List<Technique> = when (other) {
        is DealWithIncrement -> listOf(DealWithIncrement(mulMod(n, other.n, deckSize)))
        else -> error("Invalid technique combination")
    }
}

private fun String.toTechniques(deckSize: Long): List<Technique> = when {
    startsWith("cut ") -> listOf(Cut(substring(4).toLong()))
    startsWith("deal with increment ") -> listOf(DealWithIncrement(substring(20).toLong()))
    this == "deal into new stack" -> listOf(DealWithIncrement(deckSize - 1L), Cut(1L))
    else -> error("Unknown technique: $this")
}

private fun mulMod(a: Long, b: Long, mod: Long): Long {
    return a.toBigInteger().multiply(b.toBigInteger()).mod(mod.toBigInteger()).longValueExact()
}

private fun reduce(techniques: List<Technique>, deckSize: Long): List<Technique> {
    var process = techniques
    while (process.size > 2) {
        var offset = 0
        while (offset < process.size - 1) {
            if (process[offset].canBeCombinedWith(process[offset + 1])) {
                val combined = process[offset].combine(process[offset + 1], deckSize)
                process = process.subList(0, offset) + combined + process.subList(offset + 2, process.size)
                offset = max(0, offset - 1)
            } else {
                offset++
            }
        }
    }
    return process
}

private fun repeat(techniques: List<Technique>, times: Long, deckSize: Long): List<Technique> {
    var current = techniques
    val res = mutableListOf<Technique>()
    for (bit in times.toString(2).reversed()) {
        if (bit == '1') {
            res.addAll(current)
        }
        current = reduce(current + current, deckSize)
    }
    return reduce(res, deckSize)
}

private fun finalPositionForCard(card: Long, techniques: List<Technique>, deckSize: Long): Long {
    return techniques.fold(card) { pos, technique -> technique.nextPositionForCard(pos, deckSize) }
}
