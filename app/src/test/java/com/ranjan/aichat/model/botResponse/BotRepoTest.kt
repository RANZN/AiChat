package com.ranjan.aichat.model.botResponse

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class BotRepoTest {
    private lateinit var botRepo: BotRepo


    @Before
    fun setUp(){
        botRepo = BotRepoImpl()
    }

    @Test
    fun `empty message returns default response`() = runTest {
        val response: Flow<String> = botRepo.getBotResponse("")
        val emittedResponse = response.toList()

        assertEquals("Sorry we didn't get you please pardon", emittedResponse.first())
    }

    @Test
    fun `non-empty message returns response`() = runTest {
        val userMessage = "Hey!"
        val response: Flow<String> = botRepo.getBotResponse(userMessage)
        val emittedResponse = response.toList()

        assertEquals(false, emittedResponse.first().isEmpty())
    }

}