package com.ranjan.aichat.ui.mainScreen

import com.ranjan.aichat.model.Repo
import com.ranjan.aichat.model.botResponse.BotRepo
import com.ranjan.aichat.model.speech.SpeechRepo
import com.ranjan.aichat.util.speechrecognizer.SpeechEnum
import com.ranjan.aichat.util.speechrecognizer.SpeechResponse
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @Mock
    private lateinit var speechRepo: SpeechRepo

    @Mock
    private lateinit var botRepo: BotRepo

    private lateinit var mockRepo: Repo

    private lateinit var mainViewModel: MainViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val testSpeechResponse = MutableStateFlow(SpeechResponse(SpeechEnum.NONE, "Initial"))

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        mockRepo = Repo(speechRepo, botRepo)

        mainViewModel = MainViewModel(mockRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial bot message is added after delay`(): Unit = runTest {
        advanceTimeBy(1500)

        val initialMessages = mainViewModel.combinedMessages.value
        assertEquals(1, initialMessages.size)
        assertEquals(
            "Hey, I am Ranjan, Your personal AI assistant. How can I assist you?",
            initialMessages[0].text
        )
    }

    @Test
    fun `test startListening calls repo startListening`() {
        mainViewModel.startListening()
        verify(speechRepo).startListening()
    }

    @Test
    fun `test stopListening calls repo stopListening`() {
        mainViewModel.stopListening()
        verify(speechRepo).stopListening()
    }

    @Test
    fun `test adding user message`() = runTest {
        val userMessage = "Hello!"
        val botResponse = "Bot Response"
        `when`(botRepo.getBotResponse(userMessage)).thenReturn(flow { emit(botResponse) })
        mainViewModel.getResponseFromServerByBot(userMessage)

        val messages = mainViewModel.combinedMessages.value
        assertEquals(1, messages.size)
        assertEquals(userMessage, messages[0].text)
        assertEquals(true, messages[0].isSentByUser)
    }

    @Test
    fun `test getResponseFromServerByBot adds bot response`() = runTest {
        val userMessage = "Hello!"
        val botResponse = "Hi there!"

        `when`(mockRepo.getBotResponse(userMessage)).thenReturn(flow {
            emit(botResponse)
        })

        mainViewModel.getResponseFromServerByBot(userMessage)
        advanceUntilIdle()

        val messages = mainViewModel.combinedMessages.value
        assertEquals(3, messages.size)

        assertEquals(userMessage, messages[0].text)
        assertEquals(true, messages[0].isSentByUser)

        assertEquals(botResponse, messages[1].text)
        assertEquals(false, messages[1].isSentByUser)
    }

    @Test
    fun `test speechRecognitionResult emits from repo`() = runTest {
        `when`(mockRepo.speechResult).thenReturn(testSpeechResponse)

        assertEquals(testSpeechResponse.value, mainViewModel.speechRecognitionResult.value)
    }

    @Test
    fun `test onCleared calls repo onDestroy`() = runTest {
        val method = MainViewModel::class.java.getDeclaredMethod("onCleared")
        method.isAccessible = true
        method.invoke(mainViewModel)

        verify(speechRepo, times(1)).onDestroy()
    }
}