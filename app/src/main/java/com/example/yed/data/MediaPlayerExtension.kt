package com.example.yed.data

import android.media.MediaPlayer
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume

suspend fun MediaPlayer.playAudioWithResultAsync(url: String): Result<Unit> = try {
    withTimeout(2000) {
        suspendCancellableCoroutine<Result<Unit>> { cont ->
            reset()
            setDataSource(url)

            setOnErrorListener { _, what, extra ->
                if (cont.isActive) {
                    cont.resume(Result.failure(Exception("Media error: what=$what extra=$extra")))
                }
                true
            }

            setOnPreparedListener {
                if (cont.isActive) {
                    it.start()
                    cont.resume(Result.success(Unit))
                }
            }

            prepareAsync()

            cont.invokeOnCancellation {
                try {
                    stop()
                    reset()
                } catch (_: Exception) {}
            }
        }
    }
} catch (e: Exception) {
    Result.failure(e)
}