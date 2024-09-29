package com.ranjan.aichat.model.speech

import com.ranjan.aichat.util.speechrecognizer.SpeechResponse
import kotlinx.coroutines.flow.StateFlow

interface SpeechRepo {

    val speechResult: StateFlow<SpeechResponse>

    fun startListening()

    fun stopListening()

    fun onDestroy()

}