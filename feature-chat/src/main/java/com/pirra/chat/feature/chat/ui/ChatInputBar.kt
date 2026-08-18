package com.pirra.chat.feature.chat.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pirra.chat.feature.chat.audio.PirraAudioRecorder

@Composable
fun ChatInputBar(
    chatId: String,
    senderId: String,
    recipientName: String,
    replyingMessageId: String?,
    audioRecorder: PirraAudioRecorder,
    onSendTextClick: (String) -> Unit,
    onSendVoiceClick: (ByteArray) -> Unit,
    onSendImageClick: (ByteArray) -> Unit,
    context: Context,
    modifier: Modifier = Modifier
) {
    var textState by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { selectedUri ->
            try {
                val inputStream = context.contentResolver.openInputStream(selectedUri)
                val imageBytes = inputStream?.readBytes()
                inputStream?.close()

                if (imageBytes != null && imageBytes.isNotEmpty()) {
                    onSendImageClick(imageBytes)
                }
            } catch (e: Exception) {
                Log.d("ChatInputBar", "Failed to load image: ${e.localizedMessage}")
            }
        }
    }

    Surface(modifier = modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier.padding(8.dp).navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    val request = androidx.activity.result.PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        .build()

                    photoPickerLauncher.launch(request)
                }
            ) {
                Text(text = "📎", fontSize = 18.sp)
            }

            TextField(
                value = textState,
                onValueChange = { textState = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            if (textState.isNotBlank()) {
                Button(
                    onClick = {
                        onSendTextClick(textState.trim())
                        textState = ""
                    }
                ) {
                    Text("Send")
                }
            } else {
                IconButton(
                    onClick = {
                        if (!isRecording) {
                            audioRecorder.startRecording(chatId)
                            isRecording = true
                            Toast.makeText(context, "Recording started...", Toast.LENGTH_SHORT).show()
                        } else {
                            val voiceBytes = audioRecorder.stopRecording()
                            isRecording = false
                            if (voiceBytes != null && voiceBytes.isNotEmpty()) {
                                onSendVoiceClick(voiceBytes)
                            }
                        }
                    }
                ) {
                    Text(text = if (isRecording) "🔴" else "🎙️", fontSize = 18.sp)
                }
            }
        }
    }
}
