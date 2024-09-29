package com.ranjan.aichat.model.botResponse

import kotlinx.coroutines.flow.Flow

interface BotRepo {
    fun getBotResponse(message: String): Flow<String>
}