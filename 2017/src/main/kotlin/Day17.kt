object Day17 {
    private const val input = 0 // <REDACTED>

    fun part1(): Int {
        val buffer = mutableListOf(0)
        var idx = 0
        (1..2017).forEach { toInsert ->
            idx = (idx + input) % buffer.size + 1
            buffer.add(idx, toInsert)
        }
        val lastIdx = buffer.indexOf(2017)
        require(lastIdx >= 0) { "Last element not found" }
        return buffer[lastIdx + 1]
    }

    fun part2(): Int {
        var idx = 0
        var size = 1
        var nextTo0: Int? = null
        for (i in 1..50_000_000) {
            idx = (idx + input) % size + 1
            if (idx == 1) {
                nextTo0 = i
            }
            size++
        }
        return nextTo0!!
    }
}
