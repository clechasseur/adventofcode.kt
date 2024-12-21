package io.github.clechasseur.adventofcode2021

object Day14 {
    private val initial = "<REDACTED>"

    private val pairings = mapOf(
        "<REDACTED>" to '.',
    )

    fun part1(): Long = puzzle(10)

    fun part2(): Long = puzzle(40)

    private fun initialPairCounts(): Map<String, Long> = initial.zipWithNext().map { (a, b) ->
        "$a$b"
    }.groupBy { it }.mapValues { (_, l) ->
        l.size.toLong()
    }

    private fun pairCounts(): Sequence<Map<String, Long>> = generateSequence(initialPairCounts()) { prev ->
        prev.flatMap { (p, s) ->
            listOf("${p.first()}${pairings[p]!!}" to s, "${pairings[p]!!}${p.last()}" to s)
        }.groupBy { it.first }.mapValues { (_, l) -> l.sumOf { it.second } }
    }

    private fun puzzle(steps: Int): Long {
        val pc = pairCounts().drop(steps).first()
        val counts = pc.toList().groupBy { it.first.first() }.mapValues { (c, l) ->
            l.sumOf { it.second } + if (c == initial.last()) 1L else 0L
        }
        return counts.maxOf { it.value } - counts.minOf { it.value }
    }
}
