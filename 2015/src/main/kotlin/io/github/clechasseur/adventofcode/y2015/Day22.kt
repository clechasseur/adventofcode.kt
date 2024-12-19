package io.github.clechasseur.adventofcode.y2015

import kotlin.math.max

object Day22 {
    private val input = Boss(/* <REDACTED> */)

    fun part1(): Int = `fight!`(Player(), input, false)

    fun part2(): Int = `fight!`(Player(), input, true)

    private data class Player(val hp: Int, val armor: Int, val mana: Int) {
        constructor() : this(50, 0, 500)
    }

    private data class Boss(val hp: Int, val damage: Int)

    private operator fun Player.minus(cost: Int): Player {
        require(mana >= cost) { "Can't afford to pay $cost mana (mana left: $mana)" }
        return Player(hp, armor, mana - cost)
    }

    private enum class Spell(val cost: Int) {
        MAGIC_MISSILE(53),
        DRAIN(73),
        SHIELD(113),
        POISON(173),
        RECHARGE(229),
    }

    private interface Effect {
        val disallowedSpell: Spell?

        fun apply(player: Player, boss: Boss): Pair<Player, Boss>
        fun dissipate(player: Player, boss: Boss): Pair<Player, Boss> = player to boss
    }

    private class Shield : Effect {
        override val disallowedSpell: Spell?
            get() = Spell.SHIELD

        override fun apply(player: Player, boss: Boss): Pair<Player, Boss> {
            return Player(player.hp, 7, player.mana) to boss
        }

        override fun dissipate(player: Player, boss: Boss): Pair<Player, Boss> {
            return Player(player.hp, 0, player.mana) to boss
        }
    }

    private class Poison : Effect {
        override val disallowedSpell: Spell?
            get() = Spell.POISON

        override fun apply(player: Player, boss: Boss): Pair<Player, Boss> {
            return player to Boss(boss.hp - 3, boss.damage)
        }
    }

    private class Recharge : Effect {
        override val disallowedSpell: Spell?
            get() = Spell.RECHARGE

        override fun apply(player: Player, boss: Boss): Pair<Player, Boss> {
            return Player(player.hp, player.armor, player.mana + 101) to boss
        }
    }

    private data class Battlefield(
        val player: Player,
        val boss: Boss,
        val effects: List<Pair<Effect, Int>>,
        val spentMana: Int,
        val hardMode: Boolean
    ) {
        constructor(player: Player, boss: Boss, hardMode: Boolean) : this(player, boss, emptyList(), 0, hardMode)

        val done: Boolean
            get() = player.hp <= 0 || boss.hp <= 0
        val won: Boolean
            get() = done && player.hp > 0

        fun allowed(spell: Spell): Boolean = player.mana >= spell.cost && effects.none { (effect, turns) ->
            effect.disallowedSpell == spell && turns > 1
        }

        fun `fight!`(spell: Spell?): Battlefield {
            require(spell == null || allowed(spell)) { "$spell isn't allowed currently" }

            var battlefield = this
            if (battlefield.hardMode) {
                battlefield = Battlefield(
                    Player(battlefield.player.hp - 1, battlefield.player.armor, battlefield.player.mana),
                    battlefield.boss,
                    battlefield.effects,
                    battlefield.spentMana,
                    true
                )
                if (battlefield.player.hp <= 0) {
                    return battlefield
                }
            }

            battlefield = battlefield.applyEffects()
            if (battlefield.boss.hp <= 0) {
                return battlefield
            }

            if (spell != null) {
                when (spell) {
                    Spell.MAGIC_MISSILE -> battlefield = Battlefield(
                        battlefield.player - spell.cost,
                        Boss(battlefield.boss.hp - 4, battlefield.boss.damage),
                        battlefield.effects,
                        spentMana + spell.cost,
                        battlefield.hardMode
                    )
                    Spell.DRAIN -> battlefield = Battlefield(
                        Player(battlefield.player.hp + 2, battlefield.player.armor, battlefield.player.mana - spell.cost),
                        Boss(battlefield.boss.hp - 2, battlefield.boss.damage),
                        battlefield.effects,
                        spentMana + spell.cost,
                        battlefield.hardMode
                    )
                    Spell.SHIELD -> battlefield = Battlefield(
                        battlefield.player - spell.cost,
                        battlefield.boss,
                        battlefield.effects + (Shield() to 6),
                        spentMana + spell.cost,
                        battlefield.hardMode
                    )
                    Spell.POISON -> battlefield = Battlefield(
                        battlefield.player - spell.cost,
                        battlefield.boss,
                        battlefield.effects + (Poison() to 6),
                        spentMana + spell.cost,
                        battlefield.hardMode
                    )
                    Spell.RECHARGE -> battlefield = Battlefield(
                        battlefield.player - spell.cost,
                        battlefield.boss,
                        battlefield.effects + (Recharge() to 5),
                        spentMana + spell.cost,
                        battlefield.hardMode
                    )
                }
            }
            if (battlefield.boss.hp <= 0) {
                return battlefield
            }

            battlefield = battlefield.applyEffects()
            if (battlefield.boss.hp <= 0) {
                return battlefield
            }

            return Battlefield(
                Player(
                    battlefield.player.hp - max(battlefield.boss.damage - battlefield.player.armor, 1),
                    battlefield.player.armor,
                    battlefield.player.mana
                ),
                battlefield.boss,
                battlefield.effects,
                battlefield.spentMana,
                battlefield.hardMode
            )
        }

        fun possibleSpells(): List<Spell?> = Spell.values().filter { allowed(it) }.ifEmpty { listOf(null) }

        private fun applyEffects(): Battlefield {
            var newPlayer = player
            var newBoss = boss
            effects.forEach { (effect, turns) ->
                if (turns > 0) {
                    val (p, b) = effect.apply(newPlayer, newBoss)
                    newPlayer = p
                    newBoss = b
                } else {
                    val (p, b) = effect.dissipate(newPlayer, newBoss)
                    newPlayer = p
                    newBoss = b
                }
            }
            return Battlefield(newPlayer, newBoss, effects.filter { (_, turns) ->
                turns > 0
            }.map { (effect, turns) ->
                effect to turns - 1
            }, spentMana, hardMode)
        }
    }

    private fun `fight!`(player: Player, boss: Boss, hardMode: Boolean): Int {
        var battlefields = listOf(Battlefield(player, boss, hardMode))
        while (battlefields.any { !it.done }) {
            battlefields = battlefields.flatMap { battlefield ->
                if (battlefield.done) {
                    listOf(battlefield)
                } else {
                    battlefield.possibleSpells().map {
                        battlefield.`fight!`(it)
                    }.filter {
                        !it.done || it.won
                    }
                }
            }
        }
        return battlefields.minOf { it.spentMana }
    }
}
