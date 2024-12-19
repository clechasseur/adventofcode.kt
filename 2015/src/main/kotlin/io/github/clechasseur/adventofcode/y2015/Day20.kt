package io.github.clechasseur.adventofcode.y2015

object Day20 {
    private const val input = 0 // <REDACTED>

    fun part1(): Int = generateSequence(800_000) { it + 1 }.map { // hax
        it to presentsForHouse(it)
    }.dropWhile { (_, presents) ->
        presents < input
    }.first().first

    fun part2(): Int = generateSequence(800_000) { it + 1 }.map {
        it to presentsForHouse2(it)
    }.dropWhile { (_, presents) ->
        presents < input
    }.first().first

    private fun presentsForHouse(house: Int): Int = (1..house).sumOf { elf ->
        if (house % elf == 0) elf * 10 else 0
    }

    private fun presentsForHouse2(house: Int): Int = (1..house).sumOf { elf ->
        if (house % elf == 0 && elf * 50 >= house) elf * 11 else 0
    }
}
