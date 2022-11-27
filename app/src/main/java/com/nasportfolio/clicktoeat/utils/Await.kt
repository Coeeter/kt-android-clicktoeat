package com.nasportfolio.clicktoeat.utils

import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun Call.await(): Response = suspendCancellableCoroutine { continuation ->
    enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            if (continuation.isCancelled) return
            continuation.resumeWithException(e)
        }

        override fun onResponse(call: Call, response: Response) {
            if (continuation.isCancelled) return
            continuation.resume(response)
        }
    })
    continuation.invokeOnCancellation {
        try {
            cancel()
        } catch (e: Throwable) {
            Log.d("poly", e.message.toString())
        }
    }
}
