package com.ranjan.aichat.ui.chatScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

data class Message(
    val id: Long,
    var text: String,
    val isSentByUser: Boolean
)

@Composable
fun MessageItem(message: Message) {
    val backgroundColor = if (message.isSentByUser) Color.Green.copy(alpha = 0.3f) else Color.Blue.copy(alpha = 0.5f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        if (message.isSentByUser) {
            Spacer(Modifier.weight(0.1f))
        }
        Row(
            Modifier.weight(1f),
            horizontalArrangement = if (message.isSentByUser) Arrangement.End else Arrangement.Start
        ) {
            Text(
                text = message.text, modifier = Modifier
                    .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
                    .padding(12.dp)
            )

        }
        if (!message.isSentByUser) {
            Spacer(Modifier.weight(0.1f))
        }
    }
}

@Composable
fun ChatScreen(
    messageList: List<Message>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messageList.size) {
        if(messageList.isEmpty()) return@LaunchedEffect
        coroutineScope.launch {
            listState.animateScrollToItem(messageList.size - 1)
        }
    }
    LazyColumn(
        state = listState,
        modifier = modifier
            .padding(8.dp).fillMaxSize(),
    ) {
        items(messageList, key = { it.id }) {
            MessageItem(it)
        }

    }
}

@Preview
@Composable
fun ChatPreview() {
    val list = mutableListOf<Message>()
    val text =
        "asjkdfajsdfasdfasdjfhashdkjfajsdjashdfjkhasjdfjashdkfhaksjdhfaskjdfhjkasdhfkajshdfasd"
    (0..4).forEach {
        val a = it % 2 == 0
        list.add(
            Message(
                System.currentTimeMillis(),
                if (listOf(true, false).random()) "text $it" else text, a
            )
        )
    }
    ChatScreen(list)
}