package com.nasportfolio.clicktoeat.data.common

import com.google.gson.Gson
import com.nasportfolio.clicktoeat.utils.Constants.BASE_URL
import com.nasportfolio.clicktoeat.utils.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import kotlin.reflect.full.staticProperties

open class OkHttpDao(
    private val okHttpClient: OkHttpClient,
    protected val gson: Gson,
    private val path: String = "/"
) {
    companion object {
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaTypeOrNull()
        val IMAGE_MEDIA_TYPE = "image/*".toMediaTypeOrNull()
    }

    suspend fun get(endpoint: String = "/"): Response {
        val request = Request.Builder()
            .url(getUrl(endpoint))
            .build()
        return makeRequest(request)
    }

    suspend fun <T> post(endpoint: String = "/", body: T): Response {
        val requestBody = gson.toJson(body).toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url(getUrl(endpoint))
            .post(requestBody)
            .build()
        return makeRequest(request)
    }

    suspend fun <T> post(
        endpoint: String,
        body: T,
        image: File?,
        imageName: String
    ): Response {
        val multipartBuilder = MultipartBody.Builder()
        for (member in body!!::class.staticProperties) {
            var value = member.get()
            if (value !is String) value = value.toString()
            multipartBuilder.addFormDataPart(member.name, value)
        }
        image?.let {
            multipartBuilder.addFormDataPart(
                imageName,
                it.name,
                it.asRequestBody(IMAGE_MEDIA_TYPE)
            )
        }
        val request = Request.Builder()
            .url(getUrl(endpoint))
            .post(multipartBuilder.build())
            .build()
        return makeRequest(request)
    }

    suspend fun post(
        endpoint: String,
        map: Map<String, String>,
        image: File?,
        imageName: String,
    ): Response {
        val multipartBuilder = MultipartBody.Builder()
        map.forEach { (key, value) ->
            multipartBuilder.addFormDataPart(key, value)
        }
        image?.let {
            multipartBuilder.addFormDataPart(
                imageName,
                it.name,
                it.asRequestBody(IMAGE_MEDIA_TYPE)
            )
        }
        val request = Request.Builder()
            .url(getUrl(endpoint))
            .post(multipartBuilder.build())
            .build()
        return makeRequest(request)
    }

    private suspend fun makeRequest(request: Request) =
        okHttpClient.newCall(request).await()

    private fun getUrl(endpoint: String) =
        "$BASE_URL/$path/$endpoint"

}