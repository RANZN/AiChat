package com.ranjan.aichat.ui.mainScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranjan.aichat.model.Repo
import com.ranjan.aichat.ui.chatScreens.Message
import com.ranjan.aichat.util.speechrecognizer.SpeechResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MainViewModel(private val repo: Repo) : ViewModel() {

    val speechRecognitionResult: StateFlow<SpeechResponse> = repo.speechResult

    private val _combinedMessages = MutableStateFlow<List<Message>>(listOf())
    val combinedMessages: StateFlow<List<Message>> = _combinedMessages.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1.seconds)
            val initialBotMessage =
                "Hey, I am Ranjan, Your personal AI assistant. How can I assist you?"
            addMessage(Message(System.currentTimeMillis(), initialBotMessage, false))
        }
    }

    fun startListening() {
        repo.startListening()
    }

    fun stopListening() {
        repo.stopListening()
    }

    private fun addMessage(message: Message?) {
        if (message == null) return
        _combinedMessages.update { it + message }
    }

    fun getResponseFromServerByBot(messageStr: String?) {
        if (messageStr.isNullOrEmpty()) return
        val message = Message(
            System.currentTimeMillis(), messageStr, true
        )
        addMessage(message)

        viewModelScope.launch {
            repo.getBotResponse(messageStr).collect {
                addMessage(Message(System.currentTimeMillis(), it, false))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repo.onDestroy()
    }




}