package io.github.clechasseur.adventofcode.y2015

import io.github.clechasseur.adventofcode.y2015.data.Day14Data

object Day14 {
    private val input = Day14Data.input

    private val reindeerRegex =
        """^(\w+) can fly (\d+) km/s for (\d+) seconds?, but then must rest for (\d+) seconds?\.$""".toRegex()

    fun part1(): Int {
        val reindeer = input.lines().map { Reindeer(it.toReindeerSpec()) }
        return generateSequence(State(reindeer)) { it.move() }.dropWhile {
            it.turn < 2503
        }.first().reindeer.maxOf { it.first.distance }
    }

    fun part2(): Int {
        val reindeer = input.lines().map { Reindeer(it.toReindeerSpec()) }
        return generateSequence(State(reindeer)) { it.move() }.dropWhile {
            it.turn < 2503
        }.first().reindeer.maxOf { it.second }
    }

    private data class ReindeerSpec(val name: String, val speed: Int, val endurance: Int, val sleepiness: Int)

    private fun String.toReindeerSpec(): ReindeerSpec {
        val match = reindeerRegex.matchEntire(this) ?: error("Wrong reindeer spec: $this")
        val (name, speed, endurance, sleepiness) = match.destructured
        return ReindeerSpec(name, speed.toInt(), endurance.toInt(), sleepiness.toInt())
    }

    private enum class ReindeerState {
        FLYING,
        RESTING,
    }

    private class Reindeer(val spec: ReindeerSpec, val state: ReindeerState, val distance: Int, val countdown: Int) {
        constructor(spec: ReindeerSpec) : this(spec, ReindeerState.FLYING, 0, spec.endurance)

        fun move(): Reindeer = when (state) {
            ReindeerState.FLYING -> {
                if (countdown > 0) {
                    Reindeer(spec, ReindeerState.FLYING, distance + spec.speed, countdown - 1)
                } else {
                    Reindeer(spec, ReindeerState.RESTING, distance, spec.sleepiness - 1)
                }
            }
            ReindeerState.RESTING -> {
                if (countdown > 0) {
                    Reindeer(spec, ReindeerState.RESTING, distance, countdown - 1)
                } else {
                    Reindeer(spec, ReindeerState.FLYING, distance + spec.speed, spec.endurance - 1)
                }
            }
        }
    }

    private class State(val turn: Int, val reindeer: List<Pair<Reindeer, Int>>) {
        constructor(reindeer: List<Reindeer>) : this(0, reindeer.map { it to 0 })

        fun move(): State {
            val newReindeer = reindeer.map { (r, s) -> r.move() to s }
            val furthest = newReindeer.maxOf { it.first.distance }
            val scoredReindeer = newReindeer.map { (r, s) -> r to if (r.distance == furthest) s + 1 else s }
            return State(turn + 1, scoredReindeer)
        }
    }
}
