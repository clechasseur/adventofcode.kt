package io.github.clechasseur.adventofcode.y2015

import io.github.clechasseur.adventofcode.util.Pt
import io.github.clechasseur.adventofcode.y2015.data.Day6Data
import kotlin.math.max

object Day6 {
    private val input = Day6Data.input

    private val commandRegex = """(turn (?:on|off)|toggle) (\d+),(\d+) through (\d+),(\d+)""".toRegex()

    fun part1(): Int {
        val grid = mutableMapOf<Pt, Boolean>()
        input.lines().map { it.toCommand() }.forEach {
            it.actOn(grid)
        }
        return grid.count { (_, state) -> state }
    }

    fun part2(): Int {
        val grid = mutableMapOf<Pt, Int>()
        input.lines().map { it.toCommand() }.forEach {
            it.actOnIntensity(grid)
        }
        return grid.values.sum()
    }

    private enum class Action(val description: String) {
        TURN_ON("turn on"),
        TURN_OFF("turn off"),
        TOGGLE("toggle"),
    }

    private fun String.toAction(): Action = Action.values().first { it.description == this }

    private class Command(val action: Action, val topLeft: Pt, val bottomRight: Pt) {
        val lights: Sequence<Pt>
            get() = generateSequence(topLeft) { prev ->
                if (prev.x < bottomRight.x) {
                    prev + Pt(1, 0)
                } else {
                    Pt(topLeft.x, prev.y + 1)
                }
            }.takeWhile { it.y <= bottomRight.y }

        fun actOn(grid: MutableMap<Pt, Boolean>) {
            lights.forEach {
                grid[it] = actOn(grid.getOrDefault(it, false))
            }
        }

        fun actOnIntensity(grid: MutableMap<Pt, Int>) {
            lights.forEach {
                grid[it] = actOnIntensity(grid.getOrDefault(it, 0))
            }
        }

        private fun actOn(light: Boolean): Boolean = when (action) {
            Action.TURN_ON -> true
            Action.TURN_OFF -> false
            Action.TOGGLE -> !light
        }

        private fun actOnIntensity(intensity: Int): Int = when (action) {
            Action.TURN_ON -> intensity + 1
            Action.TURN_OFF -> max(intensity - 1, 0)
            Action.TOGGLE -> intensity + 2
        }
    }

    private fun String.toCommand(): Command {
        val match = commandRegex.matchEntire(this) ?: error("Wrong command format: $this")
        val (ac, tx, ty, bx, by) = match.destructured
        return Command(ac.toAction(), Pt(tx.toInt(), ty.toInt()), Pt(bx.toInt(), by.toInt()))
    }
}
