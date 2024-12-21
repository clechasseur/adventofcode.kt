package io.github.clechasseur.adventofcode2021

import io.github.clechasseur.adventofcode2021.data.Day7Data
import kotlin.math.abs

object Day7 {
    private val data = Day7Data.data

    fun part1(): Int {
        val low = data.minOrNull()!!
        val high = data.maxOrNull()!!
        return (low..high).minOf { pos ->
            data.sumOf { abs(it - pos) }
        }
    }

    fun part2(): Int {
        val low = data.minOrNull()!!
        val high = data.maxOrNull()!!
        return (low..high).minOf { pos ->
            data.sumOf { increasingFuelCost(abs(it - pos)) }
        }
    }

    private fun increasingFuelCost(dist: Int): Int {
        var i = 1
        return generateSequence { i++ }.take(dist).sum()
    }
}
