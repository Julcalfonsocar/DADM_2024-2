package co.edu.unal.Reto5

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val winningCombinations = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Rows
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Columns
        listOf(0, 4, 8), listOf(2, 4, 6) // Diagonals
    )

    private var userSoundPlayer: MediaPlayer? = null
    private var computerSoundPlayer: MediaPlayer? = null

    init {
        userSoundPlayer = MediaPlayer.create(getApplication(), R.raw.user_sound)
        computerSoundPlayer = MediaPlayer.create(getApplication(), R.raw.comp_sound)
    }

    fun onCellClicked(index: Int) {
        val currentState = _gameState.value
        if (currentState.board[index] == Player.NONE && !currentState.isGameOver) {
            // Jugada del usuario
            val newBoard = currentState.board.toMutableList().apply {
                this[index] = Player.HUMAN
            }
            
            // Verificar si el humano ganó
            if (checkWinner(newBoard, Player.HUMAN)) {
                _gameState.value = currentState.copy(
                    board = newBoard,
                    winner = Player.HUMAN,
                    isGameOver = true,
                    winningCombination = findWinningCombination(newBoard, Player.HUMAN),
                    humanWins = currentState.humanWins + 1
                )
                playUserSound()
                return
            }

            // Verificar empate
            if (newBoard.none { it == Player.NONE }) {
                _gameState.value = currentState.copy(
                    board = newBoard,
                    isGameOver = true,
                    ties = currentState.ties + 1
                )
                playUserSound()
                return
            }

            // Actualizar tablero y reproducir sonido
            _gameState.value = currentState.copy(board = newBoard)
            playUserSound()

            // Si el juego no terminó, la computadora juega después de 3 segundos
            if (!_gameState.value.isGameOver) {
                viewModelScope.launch {
                    delay(2000) // Espera 3 segundos
                    makeComputerMove()
                }
            }
        }
    }

    private fun makeComputerMove() {
        val currentState = _gameState.value
        if (!currentState.isGameOver) {
            calculateComputerMove(currentState.board)?.let { move ->
                val newBoard = currentState.board.toMutableList().apply {
                    this[move] = Player.COMPUTER
                }

                // Verificar si la computadora ganó
                if (checkWinner(newBoard, Player.COMPUTER)) {
                    _gameState.value = currentState.copy(
                        board = newBoard,
                        winner = Player.COMPUTER,
                        isGameOver = true,
                        winningCombination = findWinningCombination(newBoard, Player.COMPUTER),
                        computerWins = currentState.computerWins + 1
                    )
                    playComputerSound()
                    return
                }

                // Verificar empate
                if (newBoard.none { it == Player.NONE }) {
                    _gameState.value = currentState.copy(
                        board = newBoard,
                        isGameOver = true,
                        ties = currentState.ties + 1
                    )
                    playComputerSound()
                    return
                }

                // Actualizar tablero y reproducir sonido
                _gameState.value = currentState.copy(board = newBoard)
                playComputerSound()
            }
        }
    }

    private fun calculateComputerMove(board: List<Player>): Int? {
        return when (_gameState.value.difficulty) {
            GameDifficulty.EASY -> makeRandomMove(board)
            GameDifficulty.HARDER -> findWinningMove(board, Player.COMPUTER) ?: makeRandomMove(board)
            GameDifficulty.EXPERT -> findWinningMove(board, Player.COMPUTER) 
                ?: findWinningMove(board, Player.HUMAN) 
                ?: makeRandomMove(board)
        }
    }

    private fun makeRandomMove(board: List<Player>): Int? {
        val availableMoves = board.indices.filter { board[it] == Player.NONE }
        return availableMoves.randomOrNull()
    }

    private fun findWinningMove(board: List<Player>, player: Player): Int? {
        board.indices.forEach { index ->
            if (board[index] == Player.NONE) {
                val testBoard = board.toMutableList()
                testBoard[index] = player
                if (checkWinner(testBoard, player)) {
                    return index
                }
            }
        }
        return null
    }

    private fun checkWinner(board: List<Player>, player: Player): Boolean {
        return winningCombinations.any { combination ->
            combination.all { board[it] == player }
        }
    }

    private fun findWinningCombination(board: List<Player>, player: Player): List<Int>? {
        return winningCombinations.find { combination ->
            combination.all { board[it] == player }
        }
    }

    private fun playUserSound() {
        userSoundPlayer?.apply {
            seekTo(0)
            start()
        }
    }

    private fun playComputerSound() {
        computerSoundPlayer?.apply {
            seekTo(0)
            start()
        }
    }

    override fun onCleared() {
        super.onCleared()
        userSoundPlayer?.release()
        computerSoundPlayer?.release()
        userSoundPlayer = null
        computerSoundPlayer = null
    }

    fun setDifficulty(difficulty: GameDifficulty) {
        val currentState = _gameState.value
        _gameState.value = currentState.copy(difficulty = difficulty)
    }

    fun startNewGame() {
        val currentState = _gameState.value
        _gameState.value = GameState(
            humanWins = currentState.humanWins,
            computerWins = currentState.computerWins,
            ties = currentState.ties,
            humanStarts = !currentState.humanStarts,
            currentPlayer = if (!currentState.humanStarts) Player.HUMAN else Player.COMPUTER,
            difficulty = currentState.difficulty
        )
        
        if (!currentState.humanStarts) {
            makeComputerMove()
        }
    }
} 