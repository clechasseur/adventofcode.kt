package io.github.clechasseur.adventofcode.y2015

import kotlin.math.max

object Day21 {
    private val input = Protagonist(/* <REDACTED> */)

    fun part1(): Int = playerLayouts().sortedBy { it.totalCost }.filter { `fight!`(it) }.first().totalCost

    fun part2(): Int = playerLayouts().sortedByDescending { it.totalCost }.filter { !`fight!`(it) }.first().totalCost

    private data class Equipment(val cost: Int, val damage: Int, val armor: Int)

    private data class Protagonist(val hp: Int, val damage: Int, val armor: Int, val equipment: List<Equipment>) {
        val totalDamage: Int
            get() = damage + equipment.sumOf { it.damage }
        val totalArmor: Int
            get() = armor + equipment.sumOf { it.armor }
        val totalCost: Int
            get() = equipment.sumOf { it.cost }

        val dead: Boolean
            get() = hp <= 0

        fun afterAttack(by: Protagonist): Protagonist = Protagonist(
            hp = hp - max(by.totalDamage - totalArmor, 1),
            damage = damage,
            armor = armor,
            equipment = equipment,
        )
    }

    private fun `fight!`(startingPlayer: Protagonist): Boolean {
        var player = startingPlayer
        var boss = input
        while (true) {
            boss = boss.afterAttack(player)
            if (boss.dead) {
                return true
            }

            player = player.afterAttack(boss)
            if (player.dead) {
                return false
            }
        }
    }

    private val weapons = listOf(
        Equipment(cost = 8, damage = 4, armor = 0),
        Equipment(cost = 10, damage = 5, armor = 0),
        Equipment(cost = 25, damage = 6, armor = 0),
        Equipment(cost = 40, damage = 7, armor = 0),
        Equipment(cost = 74, damage = 8, armor = 0),
    )

    private val armors = listOf(
        Equipment(cost = 0, damage = 0, armor = 0),
        Equipment(cost = 13, damage = 0, armor = 1),
        Equipment(cost = 31, damage = 0, armor = 2),
        Equipment(cost = 53, damage = 0, armor = 3),
        Equipment(cost = 75, damage = 0, armor = 4),
        Equipment(cost = 102, damage = 0, armor = 5),
    )

    private val rings = listOf(
        Equipment(cost = 0, damage = 0, armor = 0),
        Equipment(cost = 25, damage = 1, armor = 0),
        Equipment(cost = 50, damage = 2, armor = 0),
        Equipment(cost = 100, damage = 3, armor = 0),
        Equipment(cost = 20, damage = 0, armor = 1),
        Equipment(cost = 40, damage = 0, armor = 2),
        Equipment(cost = 80, damage = 0, armor = 3),
    )

    private fun weaponLayouts(): Sequence<List<Equipment>> = weapons.asSequence().map { listOf(it) }

    private fun armorLayouts(): Sequence<List<Equipment>> = armors.asSequence().map { listOf(it) }

    private fun ringLayouts(): Sequence<List<Equipment>> = rings.indices.asSequence().flatMap { i ->
        rings.indices.asSequence().mapNotNull { j ->
            if (i != j || i == 0) listOf(rings[i], rings[j]) else null
        }
    }

    private fun playerLayouts(): Sequence<Protagonist> = weaponLayouts().flatMap { weps ->
        armorLayouts().flatMap { arms ->
            ringLayouts().map { rngs ->
                weps + arms + rngs
            }
        }
    }.map { Protagonist(hp = 100, damage = 0, armor = 0, equipment = it) }
}
