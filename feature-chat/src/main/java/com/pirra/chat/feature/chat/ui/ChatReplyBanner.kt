package com.pirra.chat.feature.chat.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pirra.chat.core.data.model.Message

@Composable
fun ChatReplyBanner(
    replyingToMessage: Message?,
    onCancelReplyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    replyingToMessage?.let { replyTarget ->
        Surface(modifier = modifier.fillMaxWidth(), color = Color(0xFFEEEEEE)) {
            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Replying to:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(replyTarget.text, fontSize = 14.sp, maxLines = 1, color = Color.DarkGray)
                }
                IconButton(onClick = onCancelReplyClick) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Cancel Reply",
                        Modifier.size(size = 16.dp)
                    )
                }
            }
        }
    }
}
