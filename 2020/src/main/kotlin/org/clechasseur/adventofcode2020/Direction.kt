package org.clechasseur.adventofcode2020

enum class Direction(val c: Char, val displacement: Pt) {
    LEFT('L', Pt(-1, 0)),
    UP('U', Pt(0, -1)),
    RIGHT('R', Pt(1, 0)),
    DOWN('D', Pt(0, 1));

    val left: Direction get() = when (this) {
        LEFT -> DOWN
        else -> values()[ordinal - 1]
    }

    val right: Direction get() = when (this) {
        DOWN -> LEFT
        else -> values()[ordinal + 1]
    }

    companion object {
        val displacements: List<Pt> by lazy {
            values().map { it.displacement }
        }
    }
}

fun Char.toDirection(): Direction = Direction.values().first { it.c == this }

fun Pt.move(direction: Direction) = this + direction.displacement
