package com.nasportfolio.network.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.ResponseBody

suspend fun ResponseBody.toJson(): String = coroutineScope {
    val getJson = async(Dispatchers.IO) { string() }
    getJson.await()
}