package com.pirra.chat.core.crypto

import android.content.Context
import android.util.Log
import java.nio.charset.StandardCharsets
import com.sun.jna.Native

private const val SYMMETRIC_KEY_SIZE = 32

class PirraCryptoEngine(private val context: Context) {

    init {
        try {
            System.setProperty("jna.nounpack", "true")
            System.setProperty("jna.boot.library.path", "")
            Native.register(PirraCryptoEngine::class.java, "pirra_crypto")
            System.loadLibrary("pirra_crypto")
        } catch (e: Throwable) {
            Log.e("PirraCrypto", "Critical error: Native layer (PCE) connection failed: ${e.localizedMessage}")
            e.printStackTrace()
        }
    }

    fun encryptMessage(plaintext: String, key: ByteArray): ByteArray {
        val plaintextBytes = plaintext.toByteArray(StandardCharsets.UTF_8)
        val encryptedUBytes = encrypt(plaintextBytes.decodeToString(), key)
        return encryptedUBytes.map { it }.toByteArray()
    }

    fun decryptMessage(ciphertext: ByteArray, key: ByteArray): String {
        val decryptedString = decrypt(data = ciphertext, key = key)
        return String(decryptedString.toByteArray(StandardCharsets.UTF_8), StandardCharsets.UTF_8)
    }

    fun generateLocalSymmetricKey(): ByteArray {
        return ByteArray(SYMMETRIC_KEY_SIZE) { 0x01.toByte() }
    }
}
