package com.pirra.chat.feature.chat.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.FileOutputStream

class PirraAudioRecorder(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var recordFile: File? = null

    fun startRecording(chatId: String) {
        try {
            // Force create an absolute reliable physical file location inside the internal app storage directory
            val internalFile = File(context.filesDir, "pirra_voice_capture.3gp").apply {
                if (exists()) delete()
                createNewFile()
            }
            recordFile = internalFile

            // Open a secure hardware streaming pipeline straight to the file descriptor allocation
            val fileOutputStream = FileOutputStream(internalFile)

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION") MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP) // Fixed low-overhead container scheme
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)   // Resilient low-bitrate compression coder
                setOutputFile(fileOutputStream.fd) // ✨ THE ENGINE FIX: Direct atomic piping via OS file descriptors!
                prepare()
                start()
            }
            android.util.Log.d("PirraAudio", "🎙️ Hardware Mic Capture successfully initialized and active.")
        } catch (e: Exception) {
            android.util.Log.e("PirraAudio", "❌ Critical failure during startRecording setup: ${e.localizedMessage}")
        }
    }

    fun stopRecording(): ByteArray? {
        try {
            mediaRecorder?.apply {
                stop()
                reset()
                release()
            }
        } catch (e: Exception) {
            android.util.Log.e("PirraAudio", "⚠️ Hardware stop capture pipeline reset failed: ${e.localizedMessage}")
        }
        mediaRecorder = null

        // Give the Android OS layout thread 150 milliseconds to flush bits to the hardware sector
        try { Thread.sleep(150) } catch (_: Exception) {}

        val fileBytes = recordFile?.let { file ->
            if (file.exists() && file.length() > 0) {
                val bytes = file.readBytes()
                android.util.Log.d("PirraAudio", "🎯 Success! Extracted pure binary audio size: ${bytes.size} bytes.")
                bytes
            } else {
                android.util.Log.e("PirraAudio", "❌ Target voice file is completely missing or size is 0 bytes!")
                null
            }
        }

        // Keep local storage sanitized by cleaning up the staging file
        try { recordFile?.delete() } catch (_: Exception) {}
        recordFile = null

        return fileBytes
    }
}
