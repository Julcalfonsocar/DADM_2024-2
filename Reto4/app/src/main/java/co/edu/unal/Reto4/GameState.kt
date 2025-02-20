package co.edu.unal.Reto4

enum class Player {
    NONE,
    HUMAN,
    COMPUTER
}

data class GameState(
    val board: List<Player> = List(9) { Player.NONE },
    val currentPlayer: Player = Player.HUMAN,
    val winner: Player? = null,
    val isGameOver: Boolean = false,
    val winningCombination: List<Int>? = null,
    val humanWins: Int = 0,
    val computerWins: Int = 0,
    val ties: Int = 0,
    val humanStarts: Boolean = true,
    val difficulty: GameDifficulty = GameDifficulty.EXPERT
) 