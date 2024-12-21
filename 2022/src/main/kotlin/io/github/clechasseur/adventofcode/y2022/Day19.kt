package io.github.clechasseur.adventofcode.y2022

import io.github.clechasseur.adventofcode.y2022.data.Day19Data

object Day19 {
    private val input = Day19Data.input

    private val blueprintRegex = """^Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.$""".toRegex()

    fun part1(): Int = input.lines().map { it.toBlueprint() }.sumOf { it.geodesAfter(24) * it.id }

    fun part2(): Int = input.lines().take(3).map { it.toBlueprint().geodesAfter(32) }.reduce { a, b -> a * b }

    private fun Blueprint.geodesAfter(minutes: Int): Int = generateSequence(setOf(State(this)) to 1) { (states, minute) ->
        val nextStates = states.asSequence().flatMap { it.nextStates() }.toSet()
        val mostGeodes = nextStates.maxOf { it.resources[ResourceType.GEODE]!! }
        nextStates.filter {
            mostGeodes - it.resources[ResourceType.GEODE]!! <= minutes - minute
        }.groupBy {
            it.robots
        }.values.flatMap { bestStates ->
            bestStates.filter { state -> bestStates.none {
                it != state && it.resources.all { (resource, inStock) ->
                    state.resources[resource]!! <= inStock
                }
            } }
        }.toSet() to minute + 1
    }.drop(minutes).first().first.maxOf { state ->
        state.resources[ResourceType.GEODE]!!
    }

    private enum class ResourceType {
        ORE,
        CLAY,
        OBSIDIAN,
        GEODE,
    }

    private data class RobotBlueprint(val cost: Map<ResourceType, Int>)

    private data class Blueprint(val id: Int, val robotBlueprints: Map<ResourceType, RobotBlueprint>)

    private data class State(
        val blueprint: Blueprint,
        val resources: Map<ResourceType, Int>,
        val robots: Map<ResourceType, Int>,
    ) {
        constructor(blueprint: Blueprint) : this(
            blueprint,
            ResourceType.values().associateWith { 0 },
            ResourceType.values().associateWith {
                if (it == ResourceType.ORE) 1 else 0
            }
        )

        fun nextStates(): List<State> {
            val possibleRobots = ResourceType.values().filter { robot ->
                robots.canBuildEventually(robot)
            }.map { robot ->
                if (resources.canBuild(robot)) robot else null
            }.distinct()
            val collected = resources.collect()
            return possibleRobots.map { robot -> copy(
                resources = collected.buildRobot(robot),
                robots = robots.addRobot(robot),
            ) }
        }

        private fun Map<ResourceType, Int>.canBuildEventually(robot: ResourceType): Boolean = blueprint.robotBlueprints[robot]!!.cost.all { (resource, _) ->
            this[resource]!! >= 1
        }

        private fun Map<ResourceType, Int>.canBuild(robot: ResourceType): Boolean = blueprint.robotBlueprints[robot]!!.cost.all { (resource, amount) ->
            this[resource]!! >= amount
        }

        private fun Map<ResourceType, Int>.collect(): Map<ResourceType, Int> = mapValues { (resource, inStock) ->
            inStock + robots[resource]!!
        }

        private fun Map<ResourceType, Int>.buildRobot(robot: ResourceType?): Map<ResourceType, Int> = mapValues { (resource, inStock) ->
            inStock - if (robot != null) blueprint.robotBlueprints[robot]!!.cost[resource] ?: 0 else 0
        }

        private fun Map<ResourceType, Int>.addRobot(robot: ResourceType?): Map<ResourceType, Int> = mapValues { (robotType, num) ->
            num + if (robotType == robot) 1 else 0
        }
    }

    private fun String.toBlueprint(): Blueprint {
        val match = blueprintRegex.matchEntire(this) ?: error("Wrong blueprint: $this")
        val (id, oreOreCost, clayOreCost, obsidianOreCost, obsidianClayCost, geodeOreCost, geodeObsidianCost) = match.destructured
        return Blueprint(
            id.toInt(),
            mapOf(
                ResourceType.ORE to RobotBlueprint(mapOf(ResourceType.ORE to oreOreCost.toInt())),
                ResourceType.CLAY to RobotBlueprint(mapOf(ResourceType.ORE to clayOreCost.toInt())),
                ResourceType.OBSIDIAN to RobotBlueprint(mapOf(
                    ResourceType.ORE to obsidianOreCost.toInt(),
                    ResourceType.CLAY to obsidianClayCost.toInt(),
                )),
                ResourceType.GEODE to RobotBlueprint(mapOf(
                    ResourceType.ORE to geodeOreCost.toInt(),
                    ResourceType.OBSIDIAN to geodeObsidianCost.toInt(),
                )),
            )
        )
    }
}
