package com.dadm.reto11

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dadm.reto11.ui.theme.Reto11Theme
import com.dadm.reto11.viewmodel.ChatViewModel
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import android.view.inputmethod.InputMethodManager
import androidx.compose.ui.platform.LocalContext
import android.content.Context

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Reto11Theme {
                ChatScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = viewModel()
) {
    var userInput by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    fun hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = (context as? ComponentActivity)?.currentFocus
        currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                reverseLayout = true,
                state = listState,
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages.reversed()) { message ->
                    ChatBubble(
                        message = message.content,
                        isUserMessage = message.isUserMessage
                    )
                }
            }
        }

        ChatInput(
            value = userInput,
            onValueChange = { userInput = it },
            onSendClick = {
                if (userInput.isNotBlank()) {
                    viewModel.sendMessage(userInput)
                    userInput = ""
                    hideKeyboard()
                }
            },
            modifier = Modifier.imePadding()
        )
    }
}

@Composable
fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .imePadding(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                placeholder = { Text("Escribe un mensaje...") },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                maxLines = 3
            )
            Button(
                onClick = onSendClick,
                enabled = value.isNotBlank(),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Enviar"
                )
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: String,
    isUserMessage: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = if (isUserMessage) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = if (isUserMessage) 
                MaterialTheme.colorScheme.primary 
            else 
                Color(0xFFE0E0E0), // Color gris claro para mensajes del bot
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUserMessage) 16.dp else 4.dp,
                bottomEnd = if (isUserMessage) 4.dp else 16.dp
            ),
            tonalElevation = 2.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(12.dp),
                color = if (isUserMessage) 
                    MaterialTheme.colorScheme.onPrimary 
                else 
                    Color.Black,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}