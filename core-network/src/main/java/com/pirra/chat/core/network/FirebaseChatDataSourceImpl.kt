package com.pirra.chat.core.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pirra.chat.core.network.model.ChatDto
import com.pirra.chat.core.network.model.MessageDto
import com.pirra.chat.core.network.model.UserDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseChatDataSourceImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : FirebaseChatDataSource {

    override suspend fun sendMessage(message: MessageDto) {
        firestore.collection("chats")
            .document(message.chatId)
            .collection("messages")
            .document(message.id)
            .set(message.toMap())
            .await()
    }

    override fun observeMessages(chatId: String): Flow<List<MessageDto>> = callbackFlow {
        val listenerRegistration = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error); return@addSnapshotListener
                }
                if (snapshot != null) {
                    val messages =
                        snapshot.documents.mapNotNull { it.toObject(MessageDto::class.java) }
                    trySend(messages)
                }
            }
        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun registerUser(
        email: String,
        password: String,
        displayName: String
    ): UserDto {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = authResult.user?.uid ?: throw Exception("User registration failed.")
        val userDto = UserDto(uid = uid, displayName = displayName, email = email)

        firestore.collection("users").document(uid).set(userDto.toMap()).await()
        return userDto
    }

    override suspend fun loginUser(email: String, password: String): UserDto {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val uid = authResult.user?.uid ?: throw Exception("Login failed.")

        val snapshot = firestore.collection("users").document(uid).get().await()
        return snapshot.toObject(UserDto::class.java) ?: throw Exception("User data not found.")
    }

    override fun getCurrentUser(): UserDto? {
        val firebaseUser = auth.currentUser ?: return null
        return UserDto(
            uid = firebaseUser.uid,
            displayName = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: ""
        )
    }

    override suspend fun logoutUser() {
        auth.signOut()
    }

    override suspend fun searchUserByEmail(email: String): UserDto? {
        val snapshot = firestore.collection("users")
            .whereEqualTo("email", email.trim())
            .get()
            .await()
        if (snapshot.isEmpty) return null
        return snapshot.documents.first().toObject(UserDto::class.java)
    }

    override suspend fun addContact(contactUid: String) {
        val currentUid = auth.currentUser?.uid ?: throw Exception("No active session found.")

        firestore.collection("users")
            .document(currentUid)
            .collection("contacts")
            .document(contactUid)
            .set(mapOf("addedAt" to FieldValue.serverTimestamp()))
            .await()
    }

    override fun observeContacts(): Flow<List<UserDto>> = callbackFlow {
        val currentUid = auth.currentUser?.uid ?: run { close(); return@callbackFlow }

        val listenerRegistration = firestore.collection("users")
            .document(currentUid)
            .collection("contacts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error); return@addSnapshotListener
                }
                if (snapshot != null) {
                    val contactIds = snapshot.documents.map { it.id }
                    if (contactIds.isEmpty()) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    firestore.collection("users")
                        .whereIn("uid", contactIds)
                        .get()
                        .addOnSuccessListener { usersSnapshot ->
                            val fullContactsList =
                                usersSnapshot.documents.mapNotNull { it.toObject(UserDto::class.java) }
                            trySend(fullContactsList)
                        }
                        .addOnFailureListener { close(it) }
                }
            }
        awaitClose { listenerRegistration.remove() }
    }

    override fun observeUserChats(): Flow<List<ChatDto>> = callbackFlow {
        val currentUid = auth.currentUser?.uid ?: run { close(); return@callbackFlow }
        val listenerRegistration = firestore.collection("users")
            .document(currentUid)
            .collection("user_chats")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error); return@addSnapshotListener
                }
                if (snapshot != null) {
                    val chats = snapshot.documents.mapNotNull { it.toObject(ChatDto::class.java) }
                    trySend(chats)
                }
            }
        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun createOrUpdateChatRoom(
        chatId: String, recipientUid: String, recipientName: String, lastText: String
    ) {
        val currentUid = auth.currentUser?.uid ?: return
        val currentUserProfile = firestore.collection("users").document(currentUid).get().await()
        val currentUserName = currentUserProfile.getString("displayName") ?: "User"

        val chatForCurrentUser = ChatDto(
            id = chatId,
            recipientUid = recipientUid,
            recipientName = recipientName,
            lastMessage = lastText,
            timestamp = System.currentTimeMillis()
        )

        val chatForRecipientUser = ChatDto(
            id = chatId,
            recipientUid = currentUid,
            recipientName = currentUserName,
            lastMessage = lastText,
            timestamp = System.currentTimeMillis()
        )

        firestore.collection("users")
            .document(currentUid)
            .collection("user_chats")
            .document(chatId)
            .set(chatForCurrentUser.toMap())
            .await()

        firestore.collection("users")
            .document(recipientUid)
            .collection("user_chats")
            .document(chatId)
            .set(chatForRecipientUser.toMap())
            .await()
    }

    override suspend fun markMessagesAsRead(chatId: String, currentUserId: String) {
        val snapshot = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .whereNotEqualTo("senderId", currentUserId)
            .get()
            .await()

        val batch = firestore.batch()
        for (document in snapshot.documents) {
            if (document.getString("status") != "READ") {
                batch.update(document.reference, "status", "READ")
            }
        }
        batch.commit().await()
    }

    override suspend fun updateUserPresence(isOnline: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(uid)
            .update(
                mapOf(
                    "isOnline" to isOnline,
                    "lastSeen" to System.currentTimeMillis()
                )
            )
            .await()
    }

    override fun observeUserPresence(userUid: String): Flow<UserDto?> = callbackFlow {
        val listenerRegistration = firestore.collection("users")
            .document(userUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                if (snapshot != null) {
                    trySend(snapshot.toObject(UserDto::class.java))
                }
            }
        awaitClose { listenerRegistration.remove() }
    }

    override fun observeTypingStatus(chatId: String, recipientUid: String): Flow<Boolean> = callbackFlow {
        val listenerRegistration = firestore.collection("chats")
            .document(chatId)
            .collection("typing_indicators")
            .document(recipientUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val isTyping = snapshot?.getBoolean("isTyping") ?: false
                trySend(isTyping)
            }
        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun setTypingStatus(chatId: String, isTyping: Boolean) {
        val currentUid = auth.currentUser?.uid ?: return
        firestore.collection("chats")
            .document(chatId)
            .collection("typing_indicators")
            .document(currentUid)
            .set(mapOf("isTyping" to isTyping, "lastUpdated" to FieldValue.serverTimestamp()))
            .await()
    }

    override suspend fun remoteDeleteMessage(chatId: String, messageId: String) {
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .document(messageId)
            .delete()
            .await()
    }

}
