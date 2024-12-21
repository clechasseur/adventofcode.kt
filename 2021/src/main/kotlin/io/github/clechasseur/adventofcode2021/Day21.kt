package io.github.clechasseur.adventofcode2021

import kotlin.math.max

object Day21 {
    private const val player1StartingPos = 0 // <REDACTED>
    private const val player2StartingPos = 0 // <REDACTED>

    fun part1(): Int = playGame(DeterministicDice())

    fun part2(): Long = playQuantumGame()

    private interface Die {
        fun roll(): Int
    }

    private class Player(var pos: Int) {
        var score: Int = 0

        fun move(die: Die): Int {
            val moveBy = die.roll() + die.roll() + die.roll()
            pos = (pos + moveBy - 1) % 10 + 1
            score += pos
            return 3
        }
    }

    private fun playGame(die: Die): Int {
        val player1 = Player(player1StartingPos)
        val player2 = Player(player2StartingPos)
        var rolls = 0
        while (true) {
            rolls += player1.move(die)
            if (player1.score >= 1000) {
                return rolls * player2.score
            }
            rolls += player2.move(die)
            if (player2.score >= 1000) {
                return rolls * player1.score
            }
        }
    }

    private class DeterministicDice : Die {
        var next: Int = 1

        override fun roll(): Int {
            val rolled = next++
            if (next > 100) {
                next = 1
            }
            return rolled
        }
    }

    private val diracDiceResults = listOf(
        1 + 1 + 1, 1 + 1 + 2, 1 + 1 + 3, 1 + 2 + 1, 1 + 2 + 2, 1 + 2 + 3, 1 + 3 + 1, 1 + 3 + 2, 1 + 3 + 3,
        2 + 1 + 1, 2 + 1 + 2, 2 + 1 + 3, 2 + 2 + 1, 2 + 2 + 2, 2 + 2 + 3, 2 + 3 + 1, 2 + 3 + 2, 2 + 3 + 3,
        3 + 1 + 1, 3 + 1 + 2, 3 + 1 + 3, 3 + 2 + 1, 3 + 2 + 2, 3 + 2 + 3, 3 + 3 + 1, 3 + 3 + 2, 3 + 3 + 3,
    )

    private data class QuantumPlayer(val pos: Int, val score: Int) {
        fun move(by: Int): QuantumPlayer {
            val newPos = (pos + by - 1) % 10 + 1
            return QuantumPlayer(newPos, score + newPos)
        }
    }

    private data class QuantumGameState(val player1: QuantumPlayer, val player2: QuantumPlayer)

    private fun playQuantumGame(): Long {
        var states: Map<QuantumGameState, Long> = mapOf(QuantumGameState(
            player1 = QuantumPlayer(player1StartingPos, 0),
            player2 = QuantumPlayer(player2StartingPos, 0),
        ) to 1L)
        var wins1 = 0L
        var wins2 = 0L

        while (states.isNotEmpty()) {
            val newStates = mutableMapOf<QuantumGameState, Long>()
            states.forEach { (state, universes) ->
                diracDiceResults.forEach { rolls ->
                    val newPlayer1 = state.player1.move(rolls)
                    if (newPlayer1.score >= 21) {
                        wins1 += universes
                    } else {
                        val newState = QuantumGameState(newPlayer1, state.player2)
                        newStates[newState] = newStates.getOrDefault(newState, 0L) + universes
                    }
                }
            }
            states = newStates.toMap()
            newStates.clear()

            states.forEach { (state, universes) ->
                diracDiceResults.forEach { rolls ->
                    val newPlayer2 = state.player2.move(rolls)
                    if (newPlayer2.score >= 21) {
                        wins2 += universes
                    } else {
                        val newState = QuantumGameState(state.player1, newPlayer2)
                        newStates[newState] = newStates.getOrDefault(newState, 0L) + universes
                    }
                }
            }
            states = newStates
        }

        return max(wins1, wins2)
    }
}
