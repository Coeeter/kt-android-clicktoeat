package com.nasportfolio.clicktoeat.data.common

import com.google.gson.Gson
import com.nasportfolio.clicktoeat.utils.Constants.BASE_URL
import com.nasportfolio.clicktoeat.utils.await
import com.nasportfolio.clicktoeat.utils.decodeFromJson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

open class OkHttpDao(
    private val okHttpClient: OkHttpClient,
    protected val gson: Gson,
    private val path: String = "/"
) {
    companion object {
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaTypeOrNull()
        val IMAGE_MEDIA_TYPE = "image/*".toMediaTypeOrNull()
    }

    fun createAuthorizationHeader(token: String) =
        "Authorization" to "Bearer $token"

    suspend fun get(
        endpoint: String = "/",
        headers: Map<String, String> = mapOf()
    ): Response {
        val request = createRequestBuilder(endpoint)
        setHeaders(request, headers)
        return makeRequest(request.build())
    }

    suspend fun <T> post(
        endpoint: String = "/",
        body: T,
        headers: Map<String, String> = mapOf()
    ): Response {
        val request = createRequestBuilder(endpoint).post(
            createJsonRequestBody(body)
        )
        setHeaders(request, headers)
        return makeRequest(request.build())
    }

    suspend fun <T> post(
        endpoint: String = "/",
        body: T,
        image: File?,
        imageName: String,
        headers: Map<String, String> = mapOf()
    ): Response {
        val request = createRequestBuilder(endpoint).post(
            createMultipartRequestBody(body, image, imageName)
        )
        setHeaders(request, headers)
        return makeRequest(request.build())
    }

    suspend fun <T> put(
        endpoint: String = "/",
        body: T,
        headers: Map<String, String> = mapOf()
    ): Response {
        val request = createRequestBuilder(endpoint).put(
            createJsonRequestBody(body)
        )
        setHeaders(request, headers)
        return makeRequest(request.build())
    }

    suspend fun <T> put(
        endpoint: String = "/",
        body: T,
        image: File?,
        imageName: String,
        headers: Map<String, String> = mapOf()
    ): Response {
        val request = createRequestBuilder(endpoint).put(
            createMultipartRequestBody(body, image, imageName)
        )
        setHeaders(request, headers)
        return makeRequest(request.build())
    }

    suspend fun <T> delete(
        endpoint: String = "/",
        body: T? = null,
        headers: Map<String, String> = mapOf()
    ): Response {
        val request = createRequestBuilder(endpoint).delete(
            createJsonRequestBody(body)
        )
        setHeaders(request, headers)
        return makeRequest(request.build())
    }

    private fun <T> createJsonRequestBody(body: T) =
        gson.toJson(body).toRequestBody(JSON_MEDIA_TYPE)

    private fun <T> createMultipartRequestBody(
        body: T,
        image: File? = null,
        imageName: String = ""
    ): RequestBody {
        val map = gson.decodeFromJson<HashMap<String, Any>>(
            gson.toJson(body)
        )
        val multipartBuilder = MultipartBody.Builder()
        map.forEach { (key, value) ->
            multipartBuilder.addFormDataPart(key, value.toString())
        }
        image?.let {
            multipartBuilder.addFormDataPart(
                imageName,
                it.name,
                image.asRequestBody(IMAGE_MEDIA_TYPE)
            )
        }
        return multipartBuilder.build()
    }

    private fun setHeaders(
        requestBuilder: Request.Builder,
        headers: Map<String, String> = mapOf()
    ) = requestBuilder.apply {
        headers.forEach { addHeader(it.key, it.value) }
    }

    private suspend fun makeRequest(request: Request) =
        okHttpClient.newCall(request).await()

    private fun createRequestBuilder(endpoint: String) =
        Request.Builder().url("$BASE_URL/$path/$endpoint")

}