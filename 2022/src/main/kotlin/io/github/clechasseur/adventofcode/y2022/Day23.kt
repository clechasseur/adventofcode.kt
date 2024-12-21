package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.util.Pt
import io.github.clechasseur.adventofcode.y2022.data.Day23Data

object Day23 {
    private val input = Day23Data.input

    fun part1(): Int = groveSequence(input.toGrove()).elementAt(10).groundCount

    fun part2(): Int = groveSequence(input.toGrove()).count()

    private enum class Direction(val displacement: Pt) {
        NW(Pt(-1, -1)),
        N(Pt(0, -1)),
        NE(Pt(1, -1)),
        E(Pt(1, 0)),
        SE(Pt(1, 1)),
        S(Pt(0, 1)),
        SW(Pt(-1, 1)),
        W(Pt(-1, 0));

        companion object {
            val allDirections = setOf(*values())
        }
    }

    private fun Pt.move(direction: Direction): Pt = this + direction.displacement

    private data class Proposal(val checks: Set<Direction>, val result: Direction)

    private val initialProposals = listOf(
        Proposal(setOf(Direction.NW, Direction.N, Direction.NE), Direction.N),
        Proposal(setOf(Direction.SW, Direction.S, Direction.SE), Direction.S),
        Proposal(setOf(Direction.NW, Direction.W, Direction.SW), Direction.W),
        Proposal(setOf(Direction.NE, Direction.E, Direction.SE), Direction.E),
    )

    private data class Grove(val elves: Set<Pt>) {
        val minX: Int
            get() = elves.minOf { it.x }
        val maxX: Int
            get() = elves.maxOf { it.x }
        val minY: Int
            get() = elves.minOf { it.y }
        val maxY: Int
            get() = elves.maxOf { it.y }

        val groundCount: Int
            get() = (minX..maxX).flatMap { x ->
                (minY..maxY).map { y -> if (elves.contains(Pt(x, y))) 0 else 1 }
            }.sum()

        fun advance(proposals: List<Proposal>): Grove {
            val moves = elves.mapNotNull { elf ->
                if (neighbourCount(elf, Direction.allDirections) > 0) {
                    proposals.firstNotNullOfOrNull { proposal ->
                        if (neighbourCount(elf, proposal.checks) == 0) {
                            PossibleMove(elf, elf.move(proposal.result))
                        } else null
                    }
                } else null
            }.fold(mutableMapOf<Pt, MutableList<PossibleMove>>()) { m, move ->
                m.getOrPut(move.destination) { mutableListOf() }.add(move)
                m
            }.filterValues { moves ->
                moves.size == 1
            }.values.associate { moves ->
                moves.first().elf to moves.first().destination
            }

            return Grove(elves.map { elf -> moves[elf] ?: elf }.toSet())
        }

        private fun neighbourCount(pt: Pt, directions: Set<Direction>): Int = directions.count { direction ->
            elves.contains(pt.move(direction))
        }

        private data class PossibleMove(val elf: Pt, val destination: Pt)
    }

    private fun proposalSequence(): Sequence<List<Proposal>> = generateSequence(initialProposals) { proposals ->
        proposals.drop(1) + proposals.first()
    }

    private fun groveSequence(initialGrove: Grove): Sequence<Grove> = sequence {
        var grove = initialGrove
        for (proposals in proposalSequence()) {
            yield(grove)
            val nextGrove = grove.advance(proposals)
            if (nextGrove == grove) {
                break
            }
            grove = nextGrove
        }
    }

    private fun String.toGrove(): Grove = Grove(lineSequence().withIndex().flatMap { (y, row) ->
        row.withIndex().mapNotNull { (x, c) -> if (c == '#') Pt(x, y) else null }
    }.toSet())
}
