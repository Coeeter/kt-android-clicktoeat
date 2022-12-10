package com.nasportfolio.clicktoeat.data.utils

import com.nasportfolio.clicktoeat.domain.utils.Resource
import com.nasportfolio.clicktoeat.domain.utils.ResourceError
import java.io.IOException

suspend fun <T> tryWithIoHandling(
    callback: suspend () -> Resource<T>
): Resource<T> {
    return try {
        callback()
    } catch (e: IOException) {
        Resource.Failure(
            ResourceError.Default(e.message.toString())
        )
    }
}