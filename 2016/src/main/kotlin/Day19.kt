object Day19 {
    private const val input = 0 // <REDACTED>

    fun part1(): Int = generateSequence(State((1..input).toList())) {
        it.next()
    }.dropWhile {
        it.elves.size > 1
    }.first().elves.single()

    fun part2(): Int = generateSequence(State2((1..input).toList())) {
        it.next()
    }.dropWhile {
        it.elves.size > 1
    }.first().elves.single()

    private data class State(val elves: List<Int>) {
        fun next(): State {
            var copyLast = false
            val nextElves = elves.asSequence().chunked(2).mapNotNull {
                copyLast = it.size == 1
                if (copyLast) null else it[0]
            }.toList()
            return State(if (copyLast) listOf(elves.last()) + nextElves else nextElves)
        }
    }

    private data class State2(val elves: List<Int>) {
        fun next(): State2 {
            val firstHalf = elves.subList(0, elves.size / 2)
            val secondHalf = elves.subList(elves.size / 2, elves.size)
            val indexesToSkip = generateSequence(0 to if (elves.size % 2 == 0) 1 else 2) { (i, s) ->
                i + s to if (s == 1) 2 else 1
            }.takeWhile { it.first in secondHalf.indices }.map { it.first }.toSet()
            val filteredSecondHalf = secondHalf.filterIndexed { i, _ -> !indexesToSkip.contains(i) }
            val newElves = firstHalf.drop(indexesToSkip.size) + filteredSecondHalf + firstHalf.take(indexesToSkip.size)
            return State2(newElves)
        }
    }
}
