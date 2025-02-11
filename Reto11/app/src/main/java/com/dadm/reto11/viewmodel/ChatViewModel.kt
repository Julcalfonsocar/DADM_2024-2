package com.dadm.reto11.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dadm.reto11.data.ChatMessage
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = "AIzaSyDP95MhcD9nU1cqyyTP0P67O4erQIrdkis"
    )
    private val chat = generativeModel.startChat()

    fun sendMessage(userMessage: String) {
        viewModelScope.launch {
            // Agregar mensaje del usuario
            _messages.value = _messages.value + ChatMessage(userMessage, true)

            try {
                // Obtener respuesta de Gemini
                val response = chat.sendMessage(userMessage)
                response.text?.let { responseText ->
                    // Agregar respuesta del bot
                    _messages.value = _messages.value + ChatMessage(responseText, false)
                }
            } catch (e: Exception) {
                // Manejar error
                _messages.value = _messages.value + ChatMessage(
                    "Lo siento, hubo un error: ${e.message}",
                    false
                )
            }
        }
    }
} 