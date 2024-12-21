package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.dij.Dijkstra
import io.github.clechasseur.adventofcode.dij.Graph
import io.github.clechasseur.adventofcode.y2022.data.Day16Data

object Day16 {
    private val input = Day16Data.input

    private val valveRegex = """^Valve ([A-Z]{2}) has flow rate=(\d+); tunnels? leads? to valves? ([A-Z]{2}(?:, [A-Z]{2})*)$""".toRegex()

    fun part1(): Int = runSimulation(0)

    fun part2(): Int = runSimulation(1)

    private fun runSimulation(numElephants: Int): Int {
        val network = Network(input.lines().map { it.toValve() })
        return network.possibleSteps(1 + numElephants, 30 - numElephants * 4).maxOf { players ->
            players.sumOf { it.releasedPressure(network) }
        }
    }

    private data class Valve(val id: String, val flowRate: Int, val tunnels: List<String>)

    private data class Network(val valves: Map<String, Valve>) : Graph<String> {
        constructor(valves: List<Valve>) : this(valves.associateBy { it.id })

        val workingValves = valves.filterValues { it.flowRate > 0 }.keys
        val dij = valves.keys.associateWith { Dijkstra.build(this, it) }

        override fun allPassable(): List<String> = valves.keys.toList()
        override fun neighbours(node: String): List<String> = valves[node]!!.tunnels
        override fun dist(a: String, b: String): Long = 1L
    }

    private data class Step(val valve: String, val openAt: Int)

    private data class Player(val steps: List<Step>) {
        fun releasedPressure(network: Network): Int = steps.sumOf { step ->
            step.openAt * network.valves[step.valve]!!.flowRate
        }
    }

    private fun Network.possibleSteps(numPlayers: Int, minutes: Int): Sequence<List<Player>> {
        return possibleSteps(workingValves, List(numPlayers) { Player(listOf(Step("AA", minutes))) })
    }

    private fun Network.possibleSteps(valves: Set<String>, soFar: List<Player>): Sequence<List<Player>> {
        val nextPlayer = soFar.maxBy { it.steps.last().openAt }
        val remainingPlayers = soFar - nextPlayer
        val curPos = nextPlayer.steps.last()
        if (curPos.openAt == 0) {
            return sequenceOf(soFar)
        }
        val nextSteps = valves.map { nextValve ->
            Step(nextValve, curPos.openAt - (dij[curPos.valve]!!.dist[nextValve]!!.toInt() + 1))
        }.filter { step ->
            step.openAt > 0
        }
        return if (nextSteps.isNotEmpty()) {
            nextSteps.asSequence().flatMap { step ->
                possibleSteps(valves - step.valve, remainingPlayers + Player(nextPlayer.steps + step))
            }
        } else {
            possibleSteps(valves, remainingPlayers + Player(nextPlayer.steps + Step("AA", 0)))
        }
    }

    private fun String.toValve(): Valve {
        val match = valveRegex.matchEntire(this) ?: error("Wrong valve spec: $this")
        val (id, flowRate, tunnels) = match.destructured
        return Valve(id, flowRate.toInt(), tunnels.split(", "))
    }
}
