package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.util.Direction
import io.github.clechasseur.adventofcode.util.Pt
import io.github.clechasseur.adventofcode.util.move
import io.github.clechasseur.adventofcode.y2022.data.Day22Data

object Day22 {
    private val mapOfBoard = Day22Data.mapOfBoard
    private const val instructions = Day22Data.instructions

    fun part1(): Int {
        val board = Board(mapOfBoard)
        val bot = Bot(Pt.ZERO, Direction.RIGHT).advance(board)
        val finalBot = bot.apply(instructions.toInstructions(), board)
        return finalBot.password
    }

    fun part2(): Int {
        val cube = Cube(mapOfBoard)
        val bot = Bot(Pt(50, 0), Direction.RIGHT)
        val finalBot = bot.apply(instructions.toInstructions(), cube)
        return finalBot.password
    }

    private val TURN_INSTRUCTIONS = setOf('L', 'R')

    private open class Board(mapOfBoard: String) {
        protected val tiles = mapOfBoard.lineSequence().flatMapIndexed { y, line ->
            line.mapIndexed { x, c -> Pt(x, y) to c }
        }.toMap().filterValues { it == '.' || it == '#' }

        protected val height = mapOfBoard.lines().size
        protected val width = mapOfBoard.lines().maxOf { it.length }

        operator fun get(pt: Pt): Char = tiles[pt] ?: ' '

        open fun move(pt: Pt, direction: Direction): Pair<Pt, Direction>? {
            val n = pt.move(direction).constraint()
            return when (val c = this[n]) {
                '.' -> n to direction
                '#' -> null
                ' ' -> move(n, direction)
                else -> error("Wrong tile: $c")
            }
        }

        private fun Pt.constraint(): Pt = Pt((x + width) % width, (y + height) % height)
    }

    private class Cube(mapOfBoard: String) : Board(mapOfBoard) {
        override fun move(pt: Pt, direction: Direction): Pair<Pt, Direction>? {
            val (n, dir) = pt.move(direction).warp(direction)
            return when (val c = this[n]) {
                '.' -> n to dir
                '#' -> null
                else -> error("Wrong tile: $c")
            }
        }

        private fun Pt.warp(direction: Direction): Pair<Pt, Direction> {
            // This method is specific to my cube shape, so you'll have to
            // roll your own if you change the cube definition

//                        6L    6D
//                     +-----+-----+
//                     |     |     |
//                  4L |  1  |  2  | 5R
//                     |     |     |
//                     +-----+-----+
//                     |     |  3R
//                  4U |  3  | 2D
//                  3L |     |
//               +-----+-----+
//               |     |     |
//            1L |  4  |  5  | 2R
//               |     |     |
//               +-----+-----+
//               |     |  6R
//            1U |  6  | 5D
//               |     |
//               +-----+
//                  2U
            
            return if (y < 0) {
                when (x / 50) {
                    1 -> Pt(0, x - 50 + 150) to Direction.RIGHT
                    2 -> Pt (x - 100, 199) to Direction.UP
                    else -> error("Wrong warp point: $this ($direction)")
                }
            } else if (y in (0 until 50)) {
                when (x / 50) {
                    0 -> Pt(0, 149 - y) to Direction.RIGHT
                    1, 2 -> this to direction
                    3 -> Pt(99, 149 - y) to Direction.LEFT
                    else -> error("Wrong warp point: $this ($direction)")
                }
            } else if (y in (50 until 100)) {
                if (x / 50 == 0 && y == 99 && direction == Direction.UP) {
                    Pt(50, x + 50) to Direction.RIGHT
                } else if (x / 50 == 0 && direction == Direction.LEFT) {
                    Pt(y - 50, 100) to Direction.DOWN
                } else if (x / 50 == 1) {
                    this to direction
                } else if (x / 50 == 2 && direction == Direction.RIGHT) {
                    Pt(y + 50, 49) to Direction.UP
                } else if (x / 50 == 2 && y == 50 && direction == Direction.DOWN) {
                    Pt(99, x - 50) to Direction.LEFT
                } else {
                    error("Wrong warp point: $this ($direction)")
                }
            } else if (y in (100 until 150)) {
                if (x < 0) {
                    Pt(50, 149 - y) to Direction.RIGHT
                } else if (x / 50 in setOf(0, 1)) {
                    this to direction
                } else if (x / 50 == 2) {
                    Pt(149, 149 - y) to Direction.LEFT
                } else {
                    error("Wrong warp point: $this ($direction)")
                }
            } else if (y in (150 until 200)) {
                if (x < 0 && direction == Direction.LEFT) {
                    Pt(y - 150 + 50, 0) to Direction.DOWN
                } else if (x / 50 == 0) {
                    this to direction
                } else if (x / 50 == 1 && direction == Direction.RIGHT) {
                    Pt(y - 100, 149) to Direction.UP
                } else if (x / 50 == 1 && y == 150 && direction == Direction.DOWN) {
                    Pt(49, x + 100) to Direction.LEFT
                } else {
                    error("Wrong warp point: $this ($direction)")
                }
            } else {
                require(y == 200 && direction == Direction.DOWN) { "Wrong warp point: $this ($direction)" }
                Pt(x + 100, 0) to Direction.DOWN
            }
        }
    }

    private data class Bot(val pos: Pt, val facing: Direction) {
        val password: Int
            get() = 1000 * (pos.y + 1) + 4 * (pos.x + 1) + when (facing) {
                Direction.RIGHT -> 0
                Direction.DOWN -> 1
                Direction.LEFT -> 2
                Direction.UP -> 3
            }

        fun advance(board: Board): Bot {
            val (newPos, newFacing) = board.move(pos, facing) ?: (pos to facing)
            return Bot(newPos, newFacing)
        }
        fun turnLeft(): Bot = Bot(pos, facing.left)
        fun turnRight(): Bot = Bot(pos, facing.right)

        fun apply(instruction: String, board: Board): Bot = when (instruction) {
            "L" -> turnLeft()
            "R" -> turnRight()
            else -> generateSequence(this) { it.advance(board) }.elementAt(instruction.toInt())
        }
        fun apply(instructions: List<String>, board: Board) = instructions.fold(this) { bot, instruction ->
            bot.apply(instruction, board)
        }
    }

    private fun String.toInstructions(): List<String> {
        val instructionList = mutableListOf<String>()
        var i = 0
        while (i in indices) {
            if (this[i] in TURN_INSTRUCTIONS) {
                instructionList.add(this[i].toString())
                i++
            } else {
                val match = """(\d+)""".toRegex().find(this, i) ?: error("Wrong instruction sequence: $this")
                instructionList.add(match.groupValues[1])
                i += match.value.length
            }
        }
        return instructionList
    }
}
