package com.pirra.chat.feature.chat.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pirra.chat.core.data.model.Message
import com.pirra.chat.core.data.model.MessageType
import com.pirra.chat.feature.chat.audio.PirraAudioPlayer
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: Message,
    isCurrentUser: Boolean,
    onDeleteClick: () -> Unit,
    onReplyClick: () -> Unit,
    audioPlayer: PirraAudioPlayer,
    context: Context
) {
    var showMenu by remember { mutableStateOf(false) }
    val bubbleColor = if (isCurrentUser) Color(0xFFDCF8C6) else Color.White
    val alignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    val shape = if (isCurrentUser) RoundedCornerShape(
        12.dp,
        12.dp,
        0.dp,
        12.dp
    ) else RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp), contentAlignment = alignment
    ) {
        Box {
            Surface(
                color = bubbleColor,
                shape = shape,
                shadowElevation = 1.dp,
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .combinedClickable(
                        onClick = { },
                        onLongClick = { showMenu = true }
                    )
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .width(IntrinsicSize.Max)
                ) {
                    message.replyText?.let { parentText ->
                        if (parentText.isNotBlank()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp),
                                color = Color.Black.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(24.dp)
                                            .background(
                                                MaterialTheme.colorScheme.primary,
                                                shape = RoundedCornerShape(2.dp)
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = parentText,
                                        fontSize = 13.sp,
                                        color = Color(0xFF333333),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                    when (message.messageType) {
                        MessageType.TEXT -> {
                            Text(text = message.text, color = Color.Black, fontSize = 16.sp)
                        }

                        MessageType.VOICE -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = "▶️ Play",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .background(
                                            Color.Black.copy(alpha = 0.05f),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .clickable {
                                            try {
                                                val rawVoiceBytes = android.util.Base64.decode(
                                                    message.text,
                                                    android.util.Base64.NO_WRAP
                                                )
                                                audioPlayer.playAudioFromBytes(rawVoiceBytes)
                                                Toast.makeText(
                                                    context,
                                                    "Playing voice clip...",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } catch (e: Exception) {
                                                Toast.makeText(
                                                    context,
                                                    "Playback error: ${e.localizedMessage}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                        .padding(6.dp)
                                )
                                Column {
                                    Text(
                                        text = "Voice Message",
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "🔒 Encrypted via Rust",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }

                        MessageType.IMAGE -> {
                            val bitmap = remember(message.text) {
                                try {
                                    val imageBytes = android.util.Base64.decode(
                                        message.text,
                                        android.util.Base64.NO_WRAP
                                    )
                                    android.graphics.BitmapFactory.decodeByteArray(
                                        imageBytes,
                                        0,
                                        imageBytes.size
                                    )
                                } catch (_: Exception) {
                                    null
                                }
                            }
                            var isFullScreenOpen by remember { mutableStateOf(false) }

                            bitmap?.let { nativeBitmap ->
                                Card(
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .width(240.dp)
                                        .height(180.dp)
                                        .clickable { isFullScreenOpen = true },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    androidx.compose.foundation.Image(
                                        bitmap = nativeBitmap.asImageBitmap(),
                                        contentDescription = "Encrypted Media Image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                }
                                if (isFullScreenOpen) {
                                    androidx.compose.ui.window.Dialog(
                                        onDismissRequest = { isFullScreenOpen = false },
                                        properties = androidx.compose.ui.window.DialogProperties(
                                            usePlatformDefaultWidth = false
                                        )
                                    ) {
                                        Surface(
                                            modifier = Modifier.fillMaxSize(),
                                            color = Color.Black
                                        ) {
                                            Box(modifier = Modifier.fillMaxSize()) {
                                                androidx.compose.foundation.Image(
                                                    bitmap = nativeBitmap.asImageBitmap(),
                                                    contentDescription = "Full Screen Viewer",
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .align(Alignment.Center),
                                                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                                                )
                                                IconButton(
                                                    onClick = { isFullScreenOpen = false },
                                                    modifier = Modifier
                                                        .padding(16.dp)
                                                        .align(Alignment.TopStart)
                                                ) {
                                                    Text("✕", color = Color.White, fontSize = 24.sp)
                                                }
                                                Button(
                                                    onClick = {
                                                        try {
                                                            val downloadDir =
                                                                android.os.Environment.getExternalStoragePublicDirectory(
                                                                    android.os.Environment.DIRECTORY_DOWNLOADS
                                                                )

                                                            val pirraDir =
                                                                java.io.File(downloadDir, "Pirra")
                                                                    .apply {
                                                                        if (!exists()) mkdirs()
                                                                    }

                                                            val filename =
                                                                "Pirra_${System.currentTimeMillis()}.jpg"

                                                            val targetDestinationFile =
                                                                java.io.File(pirraDir, filename)

                                                            val outputStream =
                                                                java.io.FileOutputStream(
                                                                    targetDestinationFile
                                                                )
                                                            nativeBitmap.compress(
                                                                android.graphics.Bitmap.CompressFormat.JPEG,
                                                                100,
                                                                outputStream
                                                            )
                                                            outputStream.flush()
                                                            outputStream.close()



                                                            android.media.MediaScannerConnection.scanFile(
                                                                context,
                                                                arrayOf(targetDestinationFile.absolutePath),
                                                                arrayOf("image/jpeg"),
                                                                null
                                                            )
                                                            Toast.makeText(
                                                                context,
                                                                "Saved to Downloads/Pirra folder!",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        } catch (e: Exception) {
                                                            Toast.makeText(
                                                                context,
                                                                "Failed to save: ${e.localizedMessage}",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    },
                                                    modifier = Modifier
                                                        .padding(16.dp)
                                                        .align(Alignment.BottomEnd)
                                                ) {
                                                    Text("Save Image")
                                                }
                                            }
                                        }
                                    }
                                }
                            } ?: Text(
                                "[Malformed Secure Image Packet]",
                                color = Color.Red,
                                fontSize = 13.sp
                            )
                        }

                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTimestamp(message.timestamp),
                            fontSize = 11.sp,
                            color = Color.Gray
                        )

                        if (isCurrentUser) {
                            Spacer(modifier = Modifier.width(4.dp))
                            val statusIndicator = when (message.status) {
                                com.pirra.chat.core.data.model.MessageStatus.SENDING -> "⏳"
                                com.pirra.chat.core.data.model.MessageStatus.SENT -> "✓"
                                com.pirra.chat.core.data.model.MessageStatus.READ -> "✓✓"
                                com.pirra.chat.core.data.model.MessageStatus.FAILED -> "⚠️"
                            }
                            Text(
                                text = statusIndicator,
                                fontSize = 12.sp,
                                color = if (message.status == com.pirra.chat.core.data.model.MessageStatus.READ) Color(
                                    0xFF34B7F1
                                ) else Color.Gray
                            )
                        }
                    }
                }
            }

            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(text = { Text("Copy Text") }, onClick = {
                    val clipboard =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    val clip = android.content.ClipData.newPlainText("PirraMessage", message.text)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                    showMenu = false
                })
                DropdownMenuItem(text = { Text("Reply Message") }, onClick = {
                    onReplyClick()
                    showMenu = false
                })
                DropdownMenuItem(text = { Text("Delete for Everyone") }, onClick = {
                    onDeleteClick()
                    showMenu = false
                })
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
