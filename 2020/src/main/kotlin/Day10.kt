object Day10 {
    private val input = listOf(
        0 // <REDACTED>
    )

    fun part1(): Int {
        val chain = listOf(0) + input.sorted() + listOf(input.max()!! + 3)
        val jmp1 = chain.zipWithNext().filter { (i1, i2) -> i2 - i1 == 1 }.count()
        val jmp3 = chain.zipWithNext().filter { (i1, i2) -> i2 - i1 == 3 }.count()
        return jmp1 * jmp3
    }

    fun part2(): Long {
        val chain = listOf(0) + input.sorted() + listOf(input.max()!! + 3)
        var total = 1L
        val curPart = mutableListOf<Int>()
        chain.forEach { a ->
            if (curPart.isEmpty() || a - curPart.last() < 3) {
                curPart.add(a)
            } else {
                total *= combinations(curPart)
                curPart.clear()
                curPart.add(a)
            }
        }
        return total
    }

    private fun combinations(chain: List<Int>): Long {
        if (chain.size <= 1) {
            return 1L
        }
        val next = chain.first()
        val thens = chain.drop(1).takeWhile { it - next <= 3 }
        return thens.map { then ->
            combinations(chain.dropWhile { it < then })
        }.sum()
    }
}