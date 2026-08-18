package com.pirra.chat

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PirraMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("PirraFCM", "🎯 New Device Registration Token generated: $token")
        // TODO: Send this token string to your custom backend server endpoints later
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("PirraFCM", "📥 Push payload message received from network boundary.")

        val title = remoteMessage.data["title"] ?: remoteMessage.notification?.title ?: "New Secure Message"
        val body = remoteMessage.data["body"] ?: remoteMessage.notification?.body ?: "🔒 Encrypted Content"
        val chatId = remoteMessage.data["chatId"] ?: ""

        sendSystemNotification(title, body, chatId)
    }

    private fun sendSystemNotification(title: String, body: String, chatId: String) {
        val channelId = "pirra_chat_messages"
        val notificationId = System.currentTimeMillis().toInt()


        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("chatId", chatId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Default built-in system safe icon asset
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true) // Automatically clears the banner from status bars upon user touch clicks
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
