package com.pirra.chat.feature.chat.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pirra.chat.feature.chat.audio.PirraAudioPlayer
import com.pirra.chat.feature.chat.audio.PirraAudioRecorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: String,
    recipientName: String,
    senderId: String,
    viewModel: ChatViewModel,
    onNavigateBack: () -> Unit
) {
    val viewState by viewModel.viewState.collectAsState()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    val audioRecorder = remember { PirraAudioRecorder(context) }
    val audioPlayer = remember { PirraAudioPlayer(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(key1 = chatId) {
        val uids = chatId.split("_")
        val recipientUid = uids.firstOrNull { it != senderId } ?: ""
        viewModel.onEvent(ChatViewEvent.LoadMessages(chatId))
        viewModel.onEvent(ChatViewEvent.ObservePresence(recipientUid))
        viewModel.onEvent(ChatViewEvent.MarkMessagesAsRead(chatId))

        if (androidx.core.content.ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.RECORD_AUDIO
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
        }
    }

    LaunchedEffect(key1 = viewState.messages.size) {
        if (viewState.messages.isNotEmpty()) {
            listState.scrollToItem(viewState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                        Column {
                            Text(text = recipientName, fontSize = 18.sp)
                            val subtitleText =
                                if (viewState.isRecipientTyping) "typing..." else if (viewState.activeRecipientPresence?.isOnline == true) "Online" else "Offline"
                            val subtitleColor =
                                if (viewState.isRecipientTyping || viewState.activeRecipientPresence?.isOnline == true) Color(
                                    0xFF4CAF50
                                ) else Color.LightGray
                            Text(text = subtitleText, fontSize = 12.sp, color = subtitleColor)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F5F5))
        ) {

            // Messages List Viewport
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewState.messages) { message ->
                    MessageBubble(
                        message = message,
                        isCurrentUser = message.senderId == senderId,
                        onDeleteClick = {
                            viewModel.onEvent(
                                ChatViewEvent.DeleteMessage(
                                    chatId,
                                    message.id
                                )
                            )
                        },
                        onReplyClick = { viewModel.onEvent(ChatViewEvent.SetReplyMessage(message)) },
                        audioPlayer = audioPlayer,
                        context = context
                    )
                }
            }

            // Segmented Reply Context Previews Component Box
            ChatReplyBanner(
                replyingToMessage = viewState.replyingToMessage,
                onCancelReplyClick = { viewModel.onEvent(ChatViewEvent.SetReplyMessage(null)) }
            )

            // Segmented Bottom Control Input Panel Board
            ChatInputBar(
                chatId = chatId,
                senderId = senderId,
                recipientName = recipientName,
                replyingMessageId = viewState.replyingToMessage?.id,
                audioRecorder = audioRecorder,
                onSendTextClick = { text ->
                    viewModel.onEvent(
                        ChatViewEvent.SendMessage(
                            chatId,
                            senderId,
                            text,
                            recipientName,
                            viewState.replyingToMessage?.id
                        )
                    )
                    viewModel.onEvent(ChatViewEvent.SetReplyMessage(null))
                },
                onSendImageClick = { imageBytes ->
                    viewModel.onEvent(
                        ChatViewEvent.SendImageMessage(
                            chatId,
                            senderId,
                            imageBytes,
                            recipientName
                        )
                    )
                },
                onSendVoiceClick = { voiceBytes ->
                    viewModel.onEvent(
                        ChatViewEvent.SendVoiceMessage(
                            chatId,
                            senderId,
                            voiceBytes,
                            recipientName
                        )
                    )
                },
                context = context
            )
        }
    }
}


//import android.content.Context
//import android.util.Log
//import android.widget.Toast
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.combinedClickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.navigationBarsPadding
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.layout.widthIn
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material3.Button
//import androidx.compose.material3.DropdownMenu
//import androidx.compose.material3.DropdownMenuItem
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.material3.TextFieldDefaults
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.pirra.chat.core.data.model.Message
//import com.pirra.chat.core.data.model.MessageStatus
//import com.pirra.chat.core.data.model.MessageType
//import com.pirra.chat.feature.chat.audio.PirraAudioPlayer
//import com.pirra.chat.feature.chat.audio.PirraAudioRecorder
//import kotlinx.coroutines.delay
//import java.text.SimpleDateFormat
//import java.util.*

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ChatScreen(
//    chatId: String,
//    recipientName: String,
//    senderId: String,
//    viewModel: ChatViewModel,
//    onNavigateBack: () -> Unit
//) {
//    val context = LocalContext.current
//    val viewState by viewModel.viewState.collectAsState()
//    var textState by remember { mutableStateOf("") }
//    val listState = rememberLazyListState()
//    val audioRecorder = remember { PirraAudioRecorder(context) }
//    var isRecording by remember { mutableStateOf(false) }
//    val audioPlayer = remember { com.pirra.chat.feature.chat.audio.PirraAudioPlayer(context) }
//
//
//    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
//        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        if (!isGranted) {
//            Toast.makeText(
//                context,
//                "Microphone permission is strictly required for voice notes!",
//                Toast.LENGTH_LONG
//            ).show()
//        }
//    }
//
//    LaunchedEffect(Unit) {
//        if (androidx.core.content.ContextCompat.checkSelfPermission(
//                context, android.Manifest.permission.RECORD_AUDIO
//            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
//        ) {
//            permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
//        }
//    }
//
//    LaunchedEffect(key1 = chatId) {
//        val uids = chatId.split("_")
//        val recipientUid = uids.firstOrNull { it != senderId } ?: ""
//        viewModel.onEvent(ChatViewEvent.LoadMessages(chatId))
//        viewModel.onEvent(ChatViewEvent.ObservePresence(recipientUid))
//        viewModel.onEvent(ChatViewEvent.MarkMessagesAsRead(chatId))
//    }
//
//    LaunchedEffect(key1 = textState) {
//        if (textState.isNotBlank()) {
//            viewModel.onEvent(ChatViewEvent.UpdateTypingStatus(chatId, true))
//            delay(2000)
//            viewModel.onEvent(ChatViewEvent.UpdateTypingStatus(chatId, false))
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(12.dp)
//                    ) {
//                        Surface(
//                            modifier = Modifier.size(40.dp),
//                            shape = CircleShape,
//                            color = Color.White.copy(alpha = 0.2f)
//                        ) {
//                            Box(contentAlignment = Alignment.Center) {
//                                Icon(
//                                    Icons.Default.Person,
//                                    contentDescription = null,
//                                    tint = Color.White
//                                )
//                            }
//                        }
//                        Column {
//                            Text(text = recipientName, fontSize = 18.sp)
//                            val subtitleText =
//                                if (viewState.isRecipientTyping) "typing..." else if (viewState.activeRecipientPresence?.isOnline == true) "Online" else "Offline"
//                            val subtitleColor =
//                                if (viewState.isRecipientTyping || viewState.activeRecipientPresence?.isOnline == true) Color(
//                                    0xFF4CAF50
//                                ) else Color.LightGray
//                            Text(text = subtitleText, fontSize = 12.sp, color = subtitleColor)
//                        }
//                    }
//                },
//                navigationIcon = {
//                    IconButton(onClick = onNavigateBack) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = null)
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    titleContentColor = Color.White,
//                    navigationIconContentColor = Color.White
//                )
//            )
//        }
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .background(Color(0xFFF5F5F5))
//        ) {
//
//            LazyColumn(
//                state = listState,
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxWidth()
//                    .padding(horizontal = 8.dp),
//                contentPadding = PaddingValues(vertical = 8.dp),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                items(viewState.messages) { message ->
//                    val isCurrentUser = message.senderId == senderId
//                    MessageBubble(
//                        message = message,
//                        isCurrentUser = isCurrentUser,
//                        onDeleteClick = {
//                            viewModel.onEvent(
//                                ChatViewEvent.DeleteMessage(
//                                    chatId,
//                                    message.id
//                                )
//                            )
//                        },
//                        onReplyClick = { viewModel.onEvent(ChatViewEvent.SetReplyMessage(message)) },
//                        audioPlayer = audioPlayer,
//                        context = context
//                    )
//                }
//            }
//
//            viewState.replyingToMessage?.let { replyTarget ->
//                Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFFEEEEEE)) {
//                    Row(
//                        modifier = Modifier.padding(8.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Column(modifier = Modifier.weight(1f)) {
//                            Text(
//                                "Replying to:",
//                                fontSize = 12.sp,
//                                color = MaterialTheme.colorScheme.primary
//                            )
//                            Text(
//                                replyTarget.text,
//                                fontSize = 14.sp,
//                                maxLines = 1,
//                                color = Color.DarkGray
//                            )
//                        }
//                        IconButton(onClick = { viewModel.onEvent(ChatViewEvent.SetReplyMessage(null)) }) {
//                            Icon(
//                                Icons.Default.Close,
//                                contentDescription = null,
//                                modifier = Modifier.size(size = 16.dp)
//                            )
//                        }
//                    }
//                }
//            }
//
//            Surface(
//                modifier = Modifier.fillMaxWidth(),
//                color = Color.White,
//                shadowElevation = 8.dp
//            ) {
//                Row(
//                    modifier = Modifier
//                        .padding(8.dp)
//                        .navigationBarsPadding(),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    TextField(
//                        value = textState,
//                        onValueChange = { textState = it },
//                        modifier = Modifier.weight(1f),
//                        placeholder = { Text("Type a message...") },
//                        colors = TextFieldDefaults.colors(
//                            focusedContainerColor = Color.Transparent,
//                            unfocusedContainerColor = Color.Transparent
//                        )
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    if (textState.isNotBlank()) {
//                        Button(
//                            onClick = {
//                                viewModel.onEvent(
//                                    ChatViewEvent.SendMessage(
//                                        chatId,
//                                        senderId,
//                                        textState.trim(),
//                                        recipientName,
//                                        viewState.replyingToMessage?.id
//                                    )
//                                )
//                                textState = ""
//                                viewModel.onEvent(ChatViewEvent.SetReplyMessage(null))
//                            }
//                        ) {
//                            Text("Send")
//                        }
//                    } else {
//                        IconButton(
//                            onClick = {
//                                if (!isRecording) {
//                                    audioRecorder.startRecording(chatId)
//                                    isRecording = true
//                                    Toast.makeText(
//                                        context,
//                                        "Recording started...",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                } else {
//                                    val voiceBytes = audioRecorder.stopRecording()
//                                    isRecording = false
//                                    if (voiceBytes != null && voiceBytes.isNotEmpty()) {
//                                        viewModel.onEvent(
//                                            ChatViewEvent.SendVoiceMessage(
//                                                chatId,
//                                                senderId,
//                                                voiceBytes,
//                                                recipientName
//                                            )
//                                        )
//                                    }
//                                }
//                            }
//                        ) {
//                            Text(text = if (isRecording) "🔴 Stop" else "🎙️", fontSize = 18.sp)
//                        }
//                    }
//
//                }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun MessageBubble(
//    message: Message,
//    isCurrentUser: Boolean,
//    onDeleteClick: () -> Unit,
//    onReplyClick: () -> Unit,
//    audioPlayer: PirraAudioPlayer,
//    context: Context
//) {
//    var showMenu by remember { mutableStateOf(false) }
//    val bubbleColor = if (isCurrentUser) Color(0xFFDCF8C6) else Color.White
//    val alignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
//    val shape = if (isCurrentUser) RoundedCornerShape(
//        12.dp,
//        12.dp,
//        0.dp,
//        12.dp
//    ) else RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp)
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 2.dp), contentAlignment = alignment
//    ) {
//        Box {
//            Surface(
//                color = bubbleColor,
//                shape = shape,
//                shadowElevation = 1.dp,
//                modifier = Modifier
//                    .widthIn(max = 280.dp)
//                    .combinedClickable(
//                        onClick = { },
//                        onLongClick = { showMenu = true }
//                    )
//            ) {
//                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
//                    Log.d("Chat Screen>>>", "Reply message: ${message.replyText}")
//                    message.replyText?.let { parentText ->
//                        if (parentText.isNotBlank()) {
//                            Surface(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(bottom = 6.dp),
//                                color = Color.Black.copy(alpha = 0.08f),
//                                shape = RoundedCornerShape(6.dp)
//                            ) {
//                                Row(
//                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    Box(
//                                        modifier = Modifier
//                                            .width(3.dp)
//                                            .height(24.dp)
//                                            .background(
//                                                MaterialTheme.colorScheme.primary,
//                                                shape = RoundedCornerShape(2.dp)
//                                            )
//                                    )
//                                    Spacer(modifier = Modifier.width(8.dp))
//                                    Text(
//                                        text = parentText,
//                                        fontSize = 13.sp,
//                                        color = Color(0xFF333333),
//                                        maxLines = 1
//                                    )
//                                }
//                            }
//                        }
//                    }
//                    if (message.messageType == MessageType.TEXT) {
//                        Text(text = message.text, color = Color.Black, fontSize = 16.sp)
//                    } else {
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.spacedBy(8.dp),
//                            modifier = Modifier.padding(vertical = 4.dp)
//                        ) {
//                            Text(
//                                text = "▶️ Play",
//                                fontSize = 14.sp,
//                                color = MaterialTheme.colorScheme.primary,
//                                modifier = Modifier
//                                    .background(
//                                        Color.Black.copy(alpha = 0.05f),
//                                        RoundedCornerShape(4.dp)
//                                    )
//                                    .clickable {
//                                        try {
//                                            val rawVoiceBytes = android.util.Base64.decode(message.text, android.util.Base64.NO_WRAP)
//                                            audioPlayer.playAudioFromBytes(rawVoiceBytes)
//                                            Toast.makeText(context, "Playing voice clip...", Toast.LENGTH_SHORT).show()
//                                        } catch (e: Exception) {
//                                            Toast.makeText(context, "Playback error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
//                                        }
//                                    }
//                                    .padding(6.dp)
//                            )
//                            Column {
//                                Text(text = "Voice Message", fontSize = 14.sp, color = Color.Black)
//                                Text(
//                                    text = "🔒 Encrypted via Rust",
//                                    fontSize = 11.sp,
//                                    color = Color.Gray
//                                )
//                            }
//                        }
//                    }
//
//                    Spacer(modifier = Modifier.height(4.dp))
//
//                    Row(
//                        modifier = Modifier.align(Alignment.End),
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(4.dp)
//                    ) {
//                        Text(
//                            text = formatTimestamp(message.timestamp),
//                            fontSize = 11.sp,
//                            color = Color.Gray
//                        )
//
//                        if (isCurrentUser) {
//                            val statusIndicator = when (message.status) {
//                                MessageStatus.SENDING -> "⏳"
//                                MessageStatus.SENT -> "✓"
//                                MessageStatus.READ -> "✓✓"
//                                MessageStatus.FAILED -> "⚠️"
//                            }
//                            Text(
//                                text = statusIndicator,
//                                fontSize = 12.sp,
//                                color = if (message.status == MessageStatus.READ) Color(0xFF34B7F1) else Color.Gray
//                            )
//                        }
//                    }
//                }
//            }
//
//            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
//                DropdownMenuItem(text = { Text("Copy Text") }, onClick = {
//                    val clipboard =
//                        context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
//                    val clip = android.content.ClipData.newPlainText("PirraMessage", message.text)
//                    clipboard.setPrimaryClip(clip)
//                    Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
//                    showMenu = false
//                })
//                DropdownMenuItem(text = { Text("Reply Message") }, onClick = {
//                    onReplyClick()
//                    showMenu = false
//                })
//                DropdownMenuItem(text = { Text("Delete for Everyone") }, onClick = {
//                    onDeleteClick()
//                    showMenu = false
//                })
//            }
//        }
//    }
//}
//
//
//fun formatTimestamp(timestamp: Long): String {
//    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
//    return sdf.format(Date(timestamp))
//}