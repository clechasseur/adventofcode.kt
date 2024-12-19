object Day6 {
    private val input = """
        <REDACTED>
    """.trimIndent().split('\t').map { it.toInt() }

    fun part1() = detectLoop().size - 1

    fun part2(): Int {
        val loop = detectLoop()
        val start = loop.indexOf(loop.last())
        return loop.indices.last - start
    }

    private fun detectLoop(): List<List<Int>> {
        val bankStates = mutableListOf<List<Int>>()
        var banks = input
        var rounds = 0
        while (!bankStates.contains(banks)) {
            bankStates.add(banks)
            banks = redistribute(banks)
            rounds++
        }
        bankStates.add(banks)
        return bankStates
    }

    private fun redistribute(banks: List<Int>): List<Int> {
        val newBanks = banks.toMutableList()
        val chosen = newBanks.indexOf(newBanks.max()!!)
        val blocks = newBanks[chosen]
        newBanks[chosen] = 0
        (1..blocks).forEach { bankIdx ->
            newBanks[(chosen + bankIdx) % newBanks.size]++
        }
        return newBanks
    }
}
