package com.ranjan.aichat.model

import com.ranjan.aichat.model.botResponse.BotRepo
import com.ranjan.aichat.model.speech.SpeechRepo

class Repo(
    private val speechRepo: SpeechRepo,
    private val botRepo: BotRepo
) : SpeechRepo by speechRepo, BotRepo by botRepo