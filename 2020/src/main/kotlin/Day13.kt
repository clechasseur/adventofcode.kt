import kotlin.math.ceil

object Day13 {
    private const val input_earliest = 0L // <REDACTED>
    private val input_bus_ids = listOf(0) // <REDACTED>

    fun part1(): Long {
        val (id, ts) = input_bus_ids.filter { it != 0 }.map { id ->
            id to busSequence(id).dropWhile { it < input_earliest }.first()
        }.minBy { it.second }!!
        return id.toLong() * (ts - input_earliest)
    }

    // I had help on this one - needed to look up the CRT online:
    // https://en.wikipedia.org/wiki/Chinese_remainder_theorem
    fun part2(): Long {
        val buses = input_bus_ids.filter { it != 0 }.map { it.toLong() }
        val offsets = input_bus_ids.mapIndexedNotNull { i, id ->
            if (id == 0) null else (id - i).toLong()
        }
        return chineseRemainder(buses, offsets)
    }

    private fun busSequence(id: Int) = generateSequence(0L) { it + id.toLong() }

    private fun chineseRemainder(n: Collection<Long>, a: Collection<Long>): Long {
        require(n.size == a.size) { "Need collections of equal size for CRT" }
        var sum = 0L
        val prod = n.reduce { acc, l -> acc * l }
        n.zip(a).forEach { (ni, ai) ->
            val p = prod / ni
            sum += ai * mulInv(p, ni) * p
        }
        return sum % prod
    }

    private fun mulInv(a: Long, b: Long): Long {
        if (b == 1L) {
            return 1L
        }
        var ab = a to b
        var x0x1 = 0L to 1L
        while (ab.first > 1) {
            val q = ab.first / ab.second
            ab = ab.second to ab.first % ab.second
            x0x1 = x0x1.second - q * x0x1.first to x0x1.first
        }
        if (x0x1.second < 0L) {
            x0x1 = x0x1.first to x0x1.second + b
        }
        return x0x1.second
    }
}