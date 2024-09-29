package com.ranjan.aichat.model.speech

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.ranjan.aichat.util.speechrecognizer.SpeechEnum
import com.ranjan.aichat.util.speechrecognizer.SpeechResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class SpeechRepoImpl(private val speechRecognizer: SpeechRecognizer) : SpeechRepo {

    private val _speechResult = MutableStateFlow(SpeechResponse(SpeechEnum.NONE))
    override val speechResult = _speechResult.asStateFlow()

    init {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _speechResult.value = SpeechResponse(SpeechEnum.READY)
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                _speechResult.value = SpeechResponse(SpeechEnum.END_OF_SPEECH)
            }

            override fun onError(errorCode: Int) {
                _speechResult.value = SpeechResponse(SpeechEnum.ERROR)
            }

            override fun onResults(results: Bundle?) {
                val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                _speechResult.value =
                    SpeechResponse(SpeechEnum.RESPONSE, data?.get(0) ?: "No result")
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val data = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                _speechResult.value =
                    SpeechResponse(SpeechEnum.PARTIAL_RESPONSE, data?.get(0) ?: "")
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    override fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
        }
        speechRecognizer.startListening(intent)
    }

    override fun stopListening() {
        speechRecognizer.stopListening()
    }

    override fun onDestroy() {
        speechRecognizer.destroy()
    }
}