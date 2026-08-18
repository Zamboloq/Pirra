package com.pirra.chat.core.data

import android.util.Base64
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.pirra.chat.core.crypto.PirraCryptoEngine
import com.pirra.chat.core.data.local.MessageDao
import com.pirra.chat.core.data.local.model.ChatEntity
import com.pirra.chat.core.data.local.model.MessageEntity
import com.pirra.chat.core.data.local.model.toDomain
import com.pirra.chat.core.data.local.model.toEntity
import com.pirra.chat.core.data.model.Message
import com.pirra.chat.core.data.model.MessageStatus
import com.pirra.chat.core.data.model.MessageType
import com.pirra.chat.core.data.model.toDomain
import com.pirra.chat.core.data.model.toDto
import com.pirra.chat.core.network.FirebaseChatDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class ChatRepositoryImpl(
    private val remoteDataSource: FirebaseChatDataSource,
    private val cryptoEngine: PirraCryptoEngine,
    private val localMessageDao: MessageDao
) : ChatRepository {

    private val secureSessionKey: ByteArray by lazy { cryptoEngine.generateLocalSymmetricKey() }
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

//    override suspend fun sendMessage(message: Message, recipientName: String) {
//        val encryptedBytes = cryptoEngine.encryptMessage(message.text, secureSessionKey)
//        val base64Ciphertext = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
//
//        val secureLocalEntity = message.copy(
//            text = base64Ciphertext,
//            status = MessageStatus.SENDING
//        ).toEntity()
//        localMessageDao.insertSingleMessage(secureLocalEntity)
//
//        try {
//            val secureMessageDto = message.copy(text = base64Ciphertext, status = MessageStatus.SENT).toDto()
//            remoteDataSource.sendMessage(secureMessageDto)
//
//            val secureSentEntity = message.copy(
//                text = base64Ciphertext,
//                status = MessageStatus.SENT
//            ).toEntity()
//            localMessageDao.insertSingleMessage(secureSentEntity)
//        } catch (e: Exception) {
//            val secureFailedEntity = message.copy(
//                text = base64Ciphertext,
//                status = MessageStatus.FAILED
//            ).toEntity()
//            localMessageDao.insertSingleMessage(secureFailedEntity)
//            throw e
//        }
//
//        val uids = message.chatId.split("_")
//        val currentUid = remoteDataSource.getCurrentUser()?.uid ?: ""
//        val recipientUid = uids.firstOrNull { it != currentUid } ?: ""
//
//        remoteDataSource.createOrUpdateChatRoom(
//            chatId = message.chatId,
//            recipientUid = recipientUid,
//            recipientName = recipientName,
//            lastText = "🔒 Encrypted Message"
//        )
//    }

//    override suspend fun sendMessage(message: Message, recipientName: String) {
//        val encryptedBytes = cryptoEngine.encryptMessage(message.text, secureSessionKey)
//        val base64Ciphertext = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
//
//        val secureLocalEntity =
//            message.copy(text = base64Ciphertext, status = MessageStatus.SENDING).toEntity()
//        localMessageDao.insertSingleMessage(secureLocalEntity)
//
//        try {
//            val secureMessageDto =
//                message.copy(text = base64Ciphertext, status = MessageStatus.SENT).toDto()
//            remoteDataSource.sendMessage(secureMessageDto)
//
//            val secureSentEntity =
//                message.copy(text = base64Ciphertext, status = MessageStatus.SENT).toEntity()
//            localMessageDao.insertSingleMessage(secureSentEntity)
//        } catch (e: Exception) {
//            val secureFailedEntity =
//                message.copy(text = base64Ciphertext, status = MessageStatus.FAILED).toEntity()
//            localMessageDao.insertSingleMessage(secureFailedEntity)
//            throw e
//        }
//
//        val uids = message.chatId.split("_")
//        val currentUid = remoteDataSource.getCurrentUser()?.uid ?: ""
//        val recipientUid = uids.firstOrNull { it != currentUid } ?: ""
//
//        remoteDataSource.createOrUpdateChatRoom(
//            chatId = message.chatId,
//            recipientUid = recipientUid,
//            recipientName = recipientName,
//            lastText = "🔒 Encrypted Message"
//        )
//
//        val localChatEntity = ChatEntity(
//            id = message.chatId,
//            recipientUid = recipientUid,
//            recipientName = recipientName,
//            lastMessage = "🔒 Encrypted Message",
//            timestamp = System.currentTimeMillis()
//        )
//        localMessageDao.insertSingleChatSummary(localChatEntity)
//    }

//    override suspend fun sendMessage(message: Message, recipientName: String) {
//        val encryptedBytes = cryptoEngine.encryptMessage(message.text, secureSessionKey)
//        val base64Ciphertext = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
//
//        val secureLocalEntity = message.copy(
//            text = base64Ciphertext,
//            status = MessageStatus.SENDING
//        ).toEntity()
//        localMessageDao.insertSingleMessage(secureLocalEntity)
//
//        try {
//            val secureMessageDto = message.copy(text = base64Ciphertext, status = MessageStatus.SENT).toDto()
//            remoteDataSource.sendMessage(secureMessageDto)
//
//            val secureSentEntity = message.copy(text = base64Ciphertext, status = MessageStatus.SENT).toEntity()
//            localMessageDao.insertSingleMessage(secureSentEntity)
//        } catch (e: Exception) {
//            val secureFailedEntity = message.copy(text = base64Ciphertext, status = MessageStatus.FAILED).toEntity()
//            localMessageDao.insertSingleMessage(secureFailedEntity)
//            throw e
//        }
//
//        val uids = message.chatId.split("_")
//        val currentUid = remoteDataSource.getCurrentUser()?.uid ?: ""
//        val recipientUid = uids.firstOrNull { it != currentUid } ?: ""
//
//        remoteDataSource.createOrUpdateChatRoom(
//            chatId = message.chatId,
//            recipientUid = recipientUid,
//            recipientName = recipientName,
//            lastText = "🔒 Encrypted Message"
//        )
//    }

    override suspend fun sendMessage(message: Message, recipientName: String) {
        val finalNetworkText: String

        if (message.messageType == MessageType.VOICE || message.messageType == MessageType.IMAGE) {
            finalNetworkText = message.text
        } else {
            val encryptedBytes = cryptoEngine.encryptMessage(message.text, secureSessionKey)
            finalNetworkText = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        }

        val secureLocalEntity = message.copy(
            text = finalNetworkText,
            status = MessageStatus.SENDING
        ).toEntity()
        localMessageDao.insertSingleMessage(secureLocalEntity)

        try {
            val secureMessageDto =
                message.copy(text = finalNetworkText, status = MessageStatus.SENT).toDto()
            remoteDataSource.sendMessage(secureMessageDto)

            val secureSentEntity =
                message.copy(text = finalNetworkText, status = MessageStatus.SENT).toEntity()
            localMessageDao.insertSingleMessage(secureSentEntity)

            triggerClientSideNotificationPush(message)
        } catch (e: Exception) {
            // 4. Fallback to FAILED state locally if network pipeline throws failure exceptions
            val secureFailedEntity =
                message.copy(text = finalNetworkText, status = MessageStatus.FAILED).toEntity()
            localMessageDao.insertSingleMessage(secureFailedEntity)
            throw e
        }

        val uids = message.chatId.split("_")
        val currentUid = remoteDataSource.getCurrentUser()?.uid ?: ""
        val recipientUid = uids.firstOrNull { it != currentUid } ?: ""

        remoteDataSource.createOrUpdateChatRoom(
            chatId = message.chatId,
            recipientUid = recipientUid,
            recipientName = recipientName,
            lastText = if (message.messageType == MessageType.VOICE) "🎙️ Voice Message" else "🔒 Encrypted Message"
        )
    }

    override fun getMessages(chatId: String): Flow<List<Message>> {
        repositoryScope.launch {
            try {
                remoteDataSource.observeMessages(chatId).collect { remoteDtoList ->
                    val entityList = remoteDtoList.map { dto ->
                        MessageEntity(
                            id = dto.id,
                            chatId = dto.chatId,
                            senderId = dto.senderId,
                            text = dto.text,
                            timestamp = dto.timestamp,
                            status = dto.status,
                            replyText = dto.replyText,
                            messageType = dto.messageType
                        )
                    }
                    localMessageDao.insertMessages(entityList)
                }
            } catch (_: Exception) {
            }
        }

        return localMessageDao.observeMessagesByChatId(chatId).map { cachedEntities ->
            cachedEntities.map { entity ->
                val domainMessage = entity.toDomain()

                if (domainMessage.messageType == MessageType.VOICE ||
                    domainMessage.messageType == MessageType.IMAGE
                ) {
                    domainMessage
                } else {
                    try {
                        val encryptedBytes = Base64.decode(domainMessage.text, Base64.NO_WRAP)
                        val clearPlaintext =
                            cryptoEngine.decryptMessage(encryptedBytes, secureSessionKey)
                        domainMessage.copy(text = clearPlaintext)
                    } catch (e: Exception) {
                        domainMessage.copy(text = "[Decryption Failure — Secure Handshake Required]")
                    }
                }
            }
        }
    }


//    override fun getMessages(chatId: String): Flow<List<Message>> {
//        repositoryScope.launch {
//            try {
//                localMessageDao.clearChatCache(chatId)
//                remoteDataSource.observeMessages(chatId).collect { remoteDtoList ->
//                    val entityList = remoteDtoList.map { dto ->
//                        MessageEntity(
//                            id = dto.id,
//                            chatId = dto.chatId,
//                            senderId = dto.senderId,
//                            messageType = dto.messageType,
//                            text = dto.text,
//                            timestamp = dto.timestamp,
//                            status = dto.status,
//                            replyText = dto.replyText
//                        )
//                    }
//                    localMessageDao.insertMessages(entityList)
//                }
//            } catch (_: Exception) {
//            }
//        }
//
//        return localMessageDao.observeMessagesByChatId(chatId).map { cachedEntities ->
//            cachedEntities.map { entity ->
//                val domainMessage = entity.toDomain()
//                if (domainMessage.messageType == com.pirra.chat.core.data.model.MessageType.VOICE) {
//                    domainMessage
//                } else {
//                    try {
//                        val encryptedBytes = Base64.decode(domainMessage.text, Base64.NO_WRAP)
//                        val clearPlaintext =
//                            cryptoEngine.decryptMessage(encryptedBytes, secureSessionKey)
//                        domainMessage.copy(text = clearPlaintext)
//                    } catch (e: Exception) {
//                        domainMessage.copy(text = "[Decryption Failure — Secure Handshake Required]")
//                    }
//                }
//            }
//        }
//    }

//    override fun getUserChatsList() =
//        remoteDataSource.observeUserChats().map { list -> list.map { it.toDomain() } }

    override fun getUserChatsList(): Flow<List<com.pirra.chat.core.data.model.Chat>> {
        repositoryScope.launch {
            try {
                remoteDataSource.observeUserChats().collect { remoteChatDtoList ->
                    val entityList = remoteChatDtoList.map { dto ->
                        ChatEntity(
                            id = dto.id,
                            recipientUid = dto.recipientUid,
                            recipientName = dto.recipientName,
                            lastMessage = dto.lastMessage,
                            timestamp = dto.timestamp
                        )
                    }
                    localMessageDao.insertChatSummaries(entityList)
                }
            } catch (_: Exception) {
            }
        }

        return localMessageDao.observeAllCachedChats().map { cachedEntities ->
            cachedEntities.map { it.toDomain() }
        }
    }

    override suspend fun register(email: String, password: String, displayName: String) =
        remoteDataSource.registerUser(email, password, displayName).toDomain()

    override suspend fun login(email: String, password: String) =
        remoteDataSource.loginUser(email, password).toDomain()

    override fun getSessionUser() = remoteDataSource.getCurrentUser()?.toDomain()
    override suspend fun logout() = remoteDataSource.logoutUser()
    override suspend fun searchUser(email: String) =
        remoteDataSource.searchUserByEmail(email)?.toDomain()

    override suspend fun appendContact(contactUid: String) = remoteDataSource.addContact(contactUid)
    override fun getContacts() =
        remoteDataSource.observeContacts().map { list -> list.map { it.toDomain() } }

    override suspend fun updatePresence(isOnline: Boolean) =
        remoteDataSource.updateUserPresence(isOnline)

    override fun getUserPresence(userUid: String) =
        remoteDataSource.observeUserPresence(userUid).map { it?.toDomain() }

    override suspend fun markAsRead(chatId: String) =
        remoteDataSource.markMessagesAsRead(
            chatId = chatId,
            currentUserId = remoteDataSource.getCurrentUser()?.uid ?: ""
        )

    override fun getTypingStatus(chatId: String, recipientUid: String): Flow<Boolean> {
        return remoteDataSource.observeTypingStatus(chatId, recipientUid)
    }

    override suspend fun sendTypingStatus(chatId: String, isTyping: Boolean) {
        remoteDataSource.setTypingStatus(chatId, isTyping)
    }

    override suspend fun deleteMessage(chatId: String, messageId: String) {
        localMessageDao.deleteMessageById(messageId)
        remoteDataSource.remoteDeleteMessage(chatId, messageId)
    }

    private fun triggerClientSideNotificationPush(message: Message) {
        CoroutineScope(Dispatchers.IO).launch {
            val participantUids = message.chatId.split("_")
            val currentUid = message.senderId
            val recipientUid = participantUids.firstOrNull { it != currentUid } ?: ""
            if (recipientUid.isBlank()) return@launch

            try {
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(recipientUid)
                    .get()
                    .addOnSuccessListener { document ->
                        val targetFcmToken = document.getString("fcmToken")
                        if (!targetFcmToken.isNullOrBlank()) {
                            val notificationBody = when (message.messageType) {
                                MessageType.VOICE -> "🎙️ Voice Message"
                                MessageType.IMAGE -> "📷 Photo Asset"
                                else -> "🔒 Encrypted Message"
                            }

                            val notificationPayloadMap = mapOf(
                                "to" to targetFcmToken,
                                "priority" to "high",
                                "notification" to mapOf(
                                    "title" to "Pirra Messenger",
                                    "body" to notificationBody,
                                    "sound" to "default"
                                ),
                                "data" to mapOf(
                                    "title" to "Pirra Messenger",
                                    "body" to notificationBody,
                                    "chatId" to message.chatId
                                )
                            )

                            try {
                                val jsonStringPayload =
                                    com.google.gson.Gson().toJson(notificationPayloadMap)
                                val networkUrlConnection = java.net.URL("https://googleapis.com")
                                    .openConnection() as java.net.HttpURLConnection

                                networkUrlConnection.requestMethod = "POST"
                                networkUrlConnection.doOutput = true
                                networkUrlConnection.setRequestProperty(
                                    "Content-Type",
                                    "application/json"
                                )

                                // TODO Note: When I deploy my custom server backend later, my server
                                //  will carry an authorized key here!
                                //  For this ad-hoc client bypass sandbox, Google allows raw
                                //  anonymous validation handshakes

                                val outputStreamWriter =
                                    java.io.OutputStreamWriter(networkUrlConnection.outputStream)
                                outputStreamWriter.write(jsonStringPayload)
                                outputStreamWriter.flush()
                                outputStreamWriter.close()

                                val responseCode = networkUrlConnection.responseCode
                                Log.d(
                                    "PirraNotificationCenter",
                                    "🚀 Pipeline Dispatch Status Code: $responseCode"
                                )
                                networkUrlConnection.disconnect()
                            } catch (networkException: Exception) {
                                Log.e(
                                    "PirraNotificationCenter",
                                    "HTTP Network stream transmission failed:" +
                                          " ${networkException.localizedMessage}"
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                Log.e(
                    "PirraNotificationCenter",
                    "Failed to query target token: ${e.localizedMessage}"
                )
            }
        }
    }

}
