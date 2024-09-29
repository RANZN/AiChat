package com.ranjan.aichat.ui.mainScreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ranjan.aichat.ui.chatScreens.ChatScreen
import com.ranjan.aichat.ui.theme.AiChatTheme
import com.ranjan.aichat.util.permission.hasPermissions
import com.ranjan.aichat.util.speechrecognizer.SpeechEnum
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AiChatTheme {
        HomeScreen()
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = koinViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                viewModel.stopListening()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap: Map<String, Boolean> ->
        if (permissionsMap.all { it.value }) {
            viewModel.startListening()
        } else {
            //TODO("Handle Permission Denied") --> not handled as more focused on architecture
        }
    }

    Column {
        val messageList by viewModel.combinedMessages.collectAsState()
        val speechResponse by viewModel.speechRecognitionResult.collectAsState()
        var speechResponseStr by remember { mutableStateOf("") }


        if(speechResponse.speechStatus == SpeechEnum.PARTIAL_RESPONSE){
            speechResponseStr = speechResponse.speechResponseText
        }

        LaunchedEffect(speechResponse.speechStatus) {
            when (speechResponse.speechStatus) {
                SpeechEnum.RESPONSE -> {
                    speechResponseStr = speechResponse.speechResponseText
                    delay(500)
                    viewModel.getResponseFromServerByBot(speechResponseStr)
                    speechResponseStr = ""
                }
                else -> {}
            }
        }

        ChatScreen(messageList, modifier.weight(1f))

        Row(
            modifier = Modifier
                .wrapContentSize()
                .border(2.dp, Color.Gray, RoundedCornerShape(5.dp))
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                text = speechResponseStr,
                fontSize = 14.sp
            )

            IconButton(modifier = Modifier
                .wrapContentSize()
                .align(Alignment.CenterVertically)
                .padding(16.dp)
                .padding(bottom = 40.dp)
                .border(2.dp, Color.Blue, RoundedCornerShape(5.dp)), onClick = {
                val permission = arrayOf(android.Manifest.permission.RECORD_AUDIO)
                if (context.hasPermissions(permission)) {
                    viewModel.startListening()
                } else {
                    launcher.launch(permission)
                }
            }) {
                ListeningAnimationIcon(micStatus = speechResponse.speechStatus)
            }
        }
    }
}

@Composable
fun ListeningAnimationIcon(micStatus: SpeechEnum) {
    val infiniteTransition = rememberInfiniteTransition()
    var isPulsing by remember { mutableStateOf(false) }

    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isPulsing) 80f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val pulseColor by infiniteTransition.animateColor(
        initialValue = Color.Yellow, targetValue = Color.Green, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    isPulsing = when (micStatus) {
        SpeechEnum.READY -> true
        SpeechEnum.PARTIAL_RESPONSE -> true
        else -> false
    }

    Box(
        contentAlignment = Alignment.Center, modifier = Modifier.size(150.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            drawRoundRect(
                color = pulseColor.copy(alpha = 0.5f),
                topLeft = Offset((size.width - pulseRadius) / 2, (size.height - pulseRadius) / 2),
                size = androidx.compose.ui.geometry.Size(pulseRadius, pulseRadius),
                cornerRadius = CornerRadius(50f, 50f)
            )
        }
        Icon(
            Icons.Filled.Mic, contentDescription = "Mic", modifier = Modifier.size(80.dp)
        )
    }
}
