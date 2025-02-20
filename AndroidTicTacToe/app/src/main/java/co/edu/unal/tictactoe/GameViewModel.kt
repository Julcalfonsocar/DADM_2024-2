package co.edu.unal.tictactoe

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val winningCombinations = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Rows
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Columns
        listOf(0, 4, 8), listOf(2, 4, 6) // Diagonals
    )

    fun onCellClicked(index: Int) {
        val currentState = _gameState.value
        
        if (currentState.board[index] != Player.NONE || currentState.isGameOver) {
            return
        }

        // Human move
        val newBoard = currentState.board.toMutableList().apply {
            this[index] = Player.HUMAN
        }
        
        // Check if human won
        if (checkWinner(newBoard, Player.HUMAN)) {
            _gameState.value = currentState.copy(
                board = newBoard,
                winner = Player.HUMAN,
                isGameOver = true,
                winningCombination = findWinningCombination(newBoard, Player.HUMAN),
                humanWins = currentState.humanWins + 1
            )
            return
        }

        // Check for draw
        if (newBoard.none { it == Player.NONE }) {
            _gameState.value = currentState.copy(
                board = newBoard,
                isGameOver = true,
                ties = currentState.ties + 1
            )
            return
        }

        // Update board and let computer move
        _gameState.value = currentState.copy(board = newBoard)
        makeComputerMove()
    }

    private fun calculateComputerMove(board: List<Player>): Int? {
        // Try to win
        val winningMove = findWinningMove(board, Player.COMPUTER)
        if (winningMove != null) return winningMove

        // Block human from winning
        val blockingMove = findWinningMove(board, Player.HUMAN)
        if (blockingMove != null) return blockingMove

        // Take a random available cell
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

    fun startNewGame() {
        val currentState = _gameState.value
        _gameState.value = GameState(
            humanWins = currentState.humanWins,
            computerWins = currentState.computerWins,
            ties = currentState.ties,
            humanStarts = !currentState.humanStarts, // Alterna quién empieza
            currentPlayer = if (!currentState.humanStarts) Player.HUMAN else Player.COMPUTER
        )
        
        // Si la computadora empieza, hace su movimiento
        if (!currentState.humanStarts) {
            makeComputerMove()
        }
    }

    private fun makeComputerMove() {
        val currentState = _gameState.value
        val newBoard = currentState.board.toMutableList()
        
        val computerMove = calculateComputerMove(newBoard)
        if (computerMove != null) {
            newBoard[computerMove] = Player.COMPUTER
            
            // Verifica si la computadora ganó
            if (checkWinner(newBoard, Player.COMPUTER)) {
                _gameState.value = currentState.copy(
                    board = newBoard,
                    winner = Player.COMPUTER,
                    isGameOver = true,
                    winningCombination = findWinningCombination(newBoard, Player.COMPUTER),
                    computerWins = currentState.computerWins + 1
                )
                return
            }
            
            // Verifica empate
            if (newBoard.none { it == Player.NONE }) {
                _gameState.value = currentState.copy(
                    board = newBoard,
                    isGameOver = true,
                    ties = currentState.ties + 1
                )
                return
            }
            
            _gameState.value = currentState.copy(board = newBoard)
        }
    }
} 