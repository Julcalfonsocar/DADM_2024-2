package co.edu.unal.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unal.tictactoe.ui.theme.*
import co.edu.unal.tictactoe.ui.theme.AndroidTicTacToeTheme

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidTicTacToeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val gameState by viewModel.gameState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Game board
        GameBoard(
            gameState = gameState,
            onCellClick = { viewModel.onCellClicked(it) }
        )

        // Game status
        GameStatus(gameState = gameState)

        // New game button
        Button(
            onClick = { viewModel.startNewGame() },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(0.dp)
        ) {
            Text(
                "New Game",
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun GameBoard(
    gameState: GameState,
    onCellClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(1f)
    ) {
        for (row in 0..2) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (col in 0..2) {
                    val index = row * 3 + col
                    GameCell(
                        cellValue = gameState.board[index],
                        isWinningCell = gameState.winningCombination?.contains(index) == true,
                        onClick = { onCellClick(index) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun GameCell(
    cellValue: Player,
    isWinningCell: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f)
            .border(
                width = 2.dp,
                color = ButtonBackgroundDark,
                shape = RoundedCornerShape(4.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (cellValue == Player.NONE) 
                ButtonBackgroundLight
            else 
                ButtonBackgroundDark
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        when (cellValue) {
            Player.HUMAN -> Text(
                "X",
                color = XColor,
                fontSize = 64.sp
            )
            Player.COMPUTER -> Text(
                "O",
                color = OColor,
                fontSize = 64.sp
            )
            Player.NONE -> Text("")
        }
    }
}

@Composable
fun GameStatus(gameState: GameState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Estado del juego actual
        Text(
            text = when {
                gameState.winner == Player.HUMAN -> "Game over: You won!"
                gameState.winner == Player.COMPUTER -> "Game over: Computer won!"
                gameState.isGameOver -> "It's a tie."
                else -> "Turn: ${if (gameState.currentPlayer == Player.HUMAN) "Human" else "Computer"}"
            },
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        )
        
        // Contador de victorias
        Text(
            text = "Human: ${gameState.humanWins}          Ties: ${gameState.ties}          Computer: ${gameState.computerWins}",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        )
    }
}