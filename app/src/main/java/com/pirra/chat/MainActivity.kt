package com.pirra.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.pirra.chat.feature.chat.RootNavGraph
import com.pirra.chat.feature.chat.ui.ChatViewModel
import com.pirra.chat.ui.theme.PirraTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.google.firebase.messaging.FirebaseMessaging


//eAMYP7a7R2awMG9ufBzMu9:APA91bFA6qYYnBUk4bcGj7fZs_zQOzH-wmn67eI-ZJocC8XCn_Ra5fC0dDp5K4RjdKba655QoL6F8_xEd3zoSNhWtk8DAhjvboqx0eakETuofKwFQpuEJ5U

class MainActivity : ComponentActivity() {

    private val chatViewModel: ChatViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RootNavGraph(viewModel = chatViewModel)
                }
            }
            PirraTheme {
                val notificationChatId = intent?.getStringExtra("chatId")
                val startDestination = if (!notificationChatId.isNullOrBlank()) {
                    "chat_screen/$notificationChatId"
                } else {
                    "home_screen"
                }
            }
        }

        val channelId = "pirra_chat_messages"
        val channelName = "Pirra Chat Messages"
        val importance = android.app.NotificationManager.IMPORTANCE_HIGH

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel =
                android.app.NotificationChannel(channelId, channelName, importance).apply {
                    description = "Secure encrypted notifications stream for Pirra platform"
                    enableLights(true)
                    lightColor = android.graphics.Color.CYAN
                    enableVibration(true)
                }

            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
            android.util.Log.d(
                "PirraNotification",
                "📢 System Notification Channel successfully registered."
            )
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                androidx.core.app.ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101
                )
            }
        }

        // Inside MainActivity.kt -> onCreate() -> Replace your FirebaseMessaging token block with this:
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                android.util.Log.w("PirraNotification", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val currentDeviceToken = task.result
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            if (currentUid.isNotBlank() && currentDeviceToken != null) {
                // ✨ THE BACKEND BRIDGE: Store the active device token under the user's secure server record
                val userTokensDatabaseReference = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUid)

                userTokensDatabaseReference.set(
                    mapOf("fcmToken" to currentDeviceToken),
                    SetOptions.merge()
                ).addOnSuccessListener {
                    android.util.Log.d("PirraNotification", "🎯 Token successfully mapped on Firestore user record.")
                }
            }
        }
    }
}
