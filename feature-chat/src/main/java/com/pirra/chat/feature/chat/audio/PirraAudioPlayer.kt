package com.pirra.chat.feature.chat.audio

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.datasource.DefaultDataSource
import java.io.File
import java.io.FileOutputStream

class PirraAudioPlayer(private val context: Context) {

    // ✨ THE ULTIMATE ARCHITECTURAL FIX: Reuse a single, long-living ExoPlayer instance
    // This totally eliminates the native hardware "BAD_INDEX" driver collision bugs on emulators!
    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(context.applicationContext).build()
    }
    private var activePlaybackFile: File? = null

    @OptIn(UnstableApi::class)
    fun playAudioFromBytes(audioBytes: ByteArray) {
        if (audioBytes.isEmpty()) return

        try {
            // Stop any current active emission before overwriting data tracks
            exoPlayer.stop()
            exoPlayer.clearMediaItems()

            // Safe file tracking synchronization on cache sector
            val localPlaybackFile = File(context.cacheDir, "pirra_voice_render.3gp").apply {
                if (exists()) delete()
                createNewFile()
            }
            activePlaybackFile = localPlaybackFile

            val fos = FileOutputStream(localPlaybackFile)
            fos.write(audioBytes)
            fos.flush()
            fos.fd.sync() // Enforce disk flush alignment routines
            fos.close()

            // Prepare the stream media source architecture
            val mediaItem = MediaItem.fromUri(Uri.fromFile(localPlaybackFile))
            val dataSourceFactory = DefaultDataSource.Factory(context)
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem)

            // ✨ Smooth playback transitions: Simply update sources on the active initialized pipeline!
            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()
            exoPlayer.play()

            android.util.Log.d("PirraAudioPlayer", "▶️ Shared ExoPlayer smoothly fed with new stream array node.")
        } catch (e: Exception) {
            android.util.Log.e("PirraAudioPlayer", "❌ Failed to execute track transition: ${e.localizedMessage}")
        }
    }

    // Call this inside screen disposal or explicit stop requests to free system handles
    fun stopAudio() {
        try {
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
        } catch (_: Exception) {}

        try {
            activePlaybackFile?.let { if (it.exists()) it.delete() }
        } catch (_: Exception) {}
        activePlaybackFile = null
    }

    // Explicit system resource release cycle hook
    fun releaseAllResources() {
        stopAudio()
        exoPlayer.release()
    }
}
