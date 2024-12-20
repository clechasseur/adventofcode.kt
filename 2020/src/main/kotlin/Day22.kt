object Day22 {
    private val deck1 = listOf(
        0 // <REDACTED>
    )
    private val deck2 = listOf(
        0 // <REDACTED>
    )

    fun part1(): Int = playTillWin(Game(setOf(Player(1, deck1), Player(2, deck2)))).winner!!.score

    fun part2(): Int = playRecursiveTillWin(Game(setOf(Player(1, deck1), Player(2, deck2)))).winner!!.score

    private data class Player(val id: Int, val deck: List<Int>) {
        val score: Int
            get() = deck.reversed().withIndex().sumBy { (it.index + 1) * it.value }

        fun winCards(cards: List<Int>): Player = copy(deck = deck.drop(1) + cards)
        fun loseTopCard(): Player = copy(deck = deck.drop(1))
    }

    private data class Game(val players: Set<Player>, val winner: Player?) {
        constructor(players: Set<Player>) : this(players, players.singleOrNull { it.deck.isNotEmpty() })

        fun playOneRound(): Game {
            val winnerLosers = players.sortedByDescending { it.deck.first() }
            return Game(
                setOf(winnerLosers.first().winCards(winnerLosers.map { it.deck.first() })) +
                winnerLosers.drop(1).map { it.loseTopCard() }
            )
        }

        fun playOneRecursiveRound(prevStates: Set<Game>): Game = when {
            prevStates.contains(this) -> Game(players, players.withId(1))
            players.all { it.deck.size > it.deck.first() } -> playSubGame()
            else -> playOneRound()
        }

        private fun playSubGame(): Game {
            val wonSubGame = playRecursiveTillWin(Game(players.map { player ->
                Player(player.id, player.deck.drop(1).take(player.deck.first()))
            }.toSet()))
            val cards = listOf(players.withId(wonSubGame.winner!!.id).deck.first()) +
                    (players - players.withId(wonSubGame.winner.id)).map { it.deck.first() }
            return Game(
                setOf(players.withId(wonSubGame.winner.id).winCards(cards)) +
                        (players - players.withId(wonSubGame.winner.id)).map { it.loseTopCard() }
            )
        }
    }

    private fun playTillWin(game: Game): Game = generateSequence(game) { prevGame ->
        if (prevGame.winner == null) prevGame.playOneRound() else null
    }.last()

    private fun playRecursiveTillWin(game: Game): Game {
        val prevStates = mutableSetOf<Game>()
        var curGame = game
        while (curGame.winner == null) {
            val newGame = curGame.playOneRecursiveRound(prevStates)
            prevStates.add(curGame)
            curGame = newGame
        }
        return curGame
    }

    private fun Set<Player>.withId(id: Int): Player = single { it.id == id }
}
