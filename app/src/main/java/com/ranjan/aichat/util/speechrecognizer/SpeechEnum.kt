package com.ranjan.aichat.util.speechrecognizer

enum class SpeechEnum {
    NONE, READY, PARTIAL_RESPONSE,RESPONSE, ERROR, END_OF_SPEECH
}

data class SpeechResponse(
    val speechStatus: SpeechEnum, val speechResponseText: String = ""
)