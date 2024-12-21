package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.util.Direction
import io.github.clechasseur.adventofcode.util.Pt
import io.github.clechasseur.adventofcode.util.move
import io.github.clechasseur.adventofcode.y2022.data.Day24Data

object Day24 {
    private val input = Day24Data.input

    fun part1(): Int = minutesToGoal(input.toValley(), thereAndBackAgain = false)

    fun part2(): Int = minutesToGoal(input.toValley(), thereAndBackAgain = true)

    private val xConstraints = 1..(input.lines().first().length - 2)
    private val yConstraints = 1..(input.lines().size - 2)

    private val entrance = Pt(xConstraints.first, 0)
    private val exit = Pt(xConstraints.last, yConstraints.last + 1)

    private val blizzardDirections = mapOf(
        '<' to Direction.LEFT,
        '^' to Direction.UP,
        '>' to Direction.RIGHT,
        'v' to Direction.DOWN,
    )

    private data class Blizzard(val pos: Pt, val direction: Direction) {
        fun move(): Blizzard {
            val newPos = pos.move(direction)
            return Blizzard(when {
                newPos.x < xConstraints.first -> Pt(xConstraints.last, newPos.y)
                newPos.x > xConstraints.last -> Pt(xConstraints.first, newPos.y)
                newPos.y < yConstraints.first -> Pt(newPos.x, yConstraints.last)
                newPos.y > yConstraints.last -> Pt(newPos.x, yConstraints.first)
                else -> newPos
            }, direction)
        }
    }

    private data class Valley(val blizzards: List<Blizzard>, val expedition: Pt) {
        fun moves(): List<Valley> {
            val possibleDestinations = Direction.values().map { expedition.move(it) } + expedition
            val newBlizzards = blizzards.map { it.move() }
            return possibleDestinations.filter { possibleDestination ->
                possibleDestination.validDestination && newBlizzards.none { it.pos == possibleDestination }
            }.map { destination ->
                Valley(newBlizzards, destination)
            }
        }

        private val Pt.validDestination: Boolean
            get() = this == entrance || this == exit || (x in xConstraints && y in yConstraints)
    }

    private fun minutesToGoal(initialValley: Valley, thereAndBackAgain: Boolean): Int {
        var valleys = setOf(initialValley)
        var minutes = 0

        while (valleys.none { it.expedition == exit }) {
            valleys = valleys.flatMap { it.moves() }.toSet()
            minutes++
        }

        if (thereAndBackAgain) {
            valleys = setOf(valleys.first { it.expedition == exit })
            while (valleys.none { it.expedition == entrance }) {
                valleys = valleys.flatMap { it.moves() }.toSet()
                minutes++
            }

            valleys = setOf(valleys.first { it.expedition == entrance })
            while (valleys.none { it.expedition == exit }) {
                valleys = valleys.flatMap { it.moves() }.toSet()
                minutes++
            }
        }

        return minutes
    }

    private fun String.toValley(): Valley = Valley(lines().withIndex().flatMap { (y, row) ->
        row.withIndex().mapNotNull { (x, c) ->
            val direction = blizzardDirections[c]
            if (direction != null) Blizzard(Pt(x, y), direction) else null
        }
    }, entrance)
}
