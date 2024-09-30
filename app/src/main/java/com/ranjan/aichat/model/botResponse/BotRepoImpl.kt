package com.ranjan.aichat.model.botResponse

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration.Companion.seconds

/**Just Dummy API model*/
private var returnResponsePos = 0

class BotRepoImpl : BotRepo {
    private val dummyResponseList = listOf(
        "Sure, I get your concern. Could you share more details?",
        "Would you mind letting me know when does this happen?",
        "Sorry could you please pardon?",
        "Okay, I got the issue, will check into my database and will share you the possible fix.",
        "END of chat......",
        "Will return dummy data now",
        "RANJAN"
    )

    override fun getBotResponse(message: String): Flow<String> = flow {
        if(message.isEmpty()) {
            emit("Sorry we didn't get you please pardon")
            return@flow
        }
        delay(1.seconds)
        val pos = if (returnResponsePos < dummyResponseList.size) returnResponsePos++ else dummyResponseList.lastIndex
        emit(dummyResponseList[pos])
    }
}