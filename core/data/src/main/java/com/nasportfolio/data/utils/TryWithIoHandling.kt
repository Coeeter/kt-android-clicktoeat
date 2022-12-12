package com.nasportfolio.data.utils

import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import java.io.IOException

suspend fun <T> tryWithIoHandling(
    callback: suspend () -> Resource<T>
): Resource<T> {
    return try {
        callback()
    } catch (e: IOException) {
        Resource.Failure(
            ResourceError.DefaultError(e.message.toString())
        )
    }
}