package co.edu.unal.Reto4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unal.Reto4.ui.theme.*
import android.app.Activity
import co.edu.unal.Reto4.ui.theme.AndroidTicTacToeTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.clickable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.Image

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
    val context = LocalContext.current
    
    // Estados para controlar la visibilidad de los diálogos
    var showDifficultyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showQuitDialog by remember { mutableStateOf(false) }

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

        // Menú
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            MenuCard(
                icon = R.drawable.ic_new_game,
                text = "New Game",
                onClick = { viewModel.startNewGame() },
                modifier = Modifier.weight(1f)
            )
            
            MenuCard(
                icon = R.drawable.ic_android,
                text = "Difficulty",
                onClick = { showDifficultyDialog = true },
                modifier = Modifier.weight(1f)
            )
            
            MenuCard(
                icon = R.drawable.ic_about,
                text = "About",
                onClick = { showAboutDialog = true },
                modifier = Modifier.weight(1f)
            )
            
            MenuCard(
                icon = R.drawable.ic_quit,
                text = "Quit",
                onClick = { showQuitDialog = true },
                modifier = Modifier.weight(1f)
            )
        }
    }

    // Diálogo de Dificultad con RadioButtons
    if (showDifficultyDialog) {
        AlertDialog(
            onDismissRequest = { showDifficultyDialog = false },
            title = { Text("Choose a difficulty level:") },
            text = {
                Column {
                    GameDifficulty.values().forEach { difficulty ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = gameState.difficulty == difficulty,
                                onClick = {
                                    viewModel.setDifficulty(difficulty)
                                    showDifficultyDialog = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MenuButtonColor,
                                    unselectedColor = MenuButtonColor.copy(alpha = 0.6f)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(difficulty.name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showDifficultyDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = MenuButtonColor)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Diálogo About con botón gris
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.tic_tac_toe),  // Usa el mismo ícono de Android
                        contentDescription = "About Icon",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(bottom = 16.dp)
                    )
                    
                    Text(
                        text = "Tic-Tac-Toe\nBy Julián Alfonso",
                        color = XColor,  // Usamos el color verde definido para las X
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Text(
                        text = "Choose from one of three difficulty levels.\nDon't let the Android beat you!",
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = MenuButtonColor)
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Diálogo Quit con botones grises
    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            title = { Text("Quit Game") },
            text = { Text("Are you sure you want to quit?") },
            confirmButton = {
                TextButton(
                    onClick = { (context as? Activity)?.finish() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MenuButtonColor)
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showQuitDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = MenuButtonColor)
                ) {
                    Text("No")
                }
            }
        )
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

@Composable
private fun MenuCard(
    icon: Int,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .fillMaxHeight()
            .then(modifier)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MenuButtonColor
        ),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                modifier = Modifier.size(40.dp),
                tint = Color.Unspecified
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = text,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}