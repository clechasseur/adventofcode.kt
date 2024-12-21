package io.github.clechasseur.adventofcode.y2022

object Day11 {
    private val input = listOf(
        Monkey(
            id = 0,
            items = listOf(0L), // <REDACTED>
            op = { it * 0L }, // <REDACTED>
            test = { if (it % 1L == 0L) 0 else 0 } // <REDACTED>
        ),
    )
    private const val clonazepam = 0L // product of all X in "divisible by X" // <REDACTED>

    fun part1(): Long = generateSequence(input) {
        it.playOneRound(boredMonkeys = true)
    }.drop(20).first().sortedByDescending {
        it.business
    }.take(2).fold(1L) { acc, monkey ->
        acc * monkey.business
    }

    fun part2(): Long = generateSequence(input) {
        it.playOneRound(boredMonkeys = false)
    }.drop(10_000).first().sortedByDescending {
        it.business
    }.take(2).fold(1L) { acc, monkey ->
        acc * monkey.business
    }

    private data class Monkey(
        val id: Int,
        val items: List<Long>,
        val op: (Long) -> Long,
        val test: (Long) -> Int,
        val business: Long = 0L
    ) {
        fun proceed(bored: Boolean): Pair<Monkey, Map<Int, List<Long>>> = copy(
            items = listOf(),
            business = business + items.size
        ) to items.map { worry ->
            val newWorry = (op(worry) / if (bored) 3L else 1L) % clonazepam
            test(newWorry) to newWorry
        }.fold(mutableMapOf()) { m, (destMonkey, item) ->
            m[destMonkey] = (m[destMonkey] ?: listOf()) + item
            m
        }

        fun add(newItems: List<Long>): Monkey = copy(items = items + newItems)
    }

    private fun List<Monkey>.playOneTurn(boredMonkeys: Boolean): List<Monkey> {
        val (newLast, result) = first().proceed(boredMonkeys)
        return drop(1).map { it.add(result[it.id] ?: listOf()) } + newLast
    }

    private fun List<Monkey>.playOneRound(boredMonkeys: Boolean): List<Monkey> = generateSequence(this) {
        it.playOneTurn(boredMonkeys)
    }.drop(size).first()
}
