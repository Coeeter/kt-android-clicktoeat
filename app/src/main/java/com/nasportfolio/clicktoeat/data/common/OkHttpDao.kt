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
    private val path: String
) {
    companion object {
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaTypeOrNull()
        val IMAGE_MEDIA_TYPE = "image/*".toMediaTypeOrNull()
        const val AUTHORIZATION = "Authorization"
        const val BEARER = "Bearer "
    }

    suspend fun get(
        endpoint: String = "/",
        headers: Map<String, String> = mapOf()
    ): Response = makeRequest(
        endpoint = endpoint,
        headers = headers,
        method = HttpMethods.GET,
    )

    suspend fun <T> post(
        endpoint: String = "/",
        body: T,
        headers: Map<String, String> = mapOf()
    ): Response = makeRequest(
        endpoint = endpoint,
        body = body,
        headers = headers,
        contentType = ContentType.JSON,
        method = HttpMethods.POST
    )

    suspend fun <T> post(
        endpoint: String = "/",
        body: T,
        file: File?,
        requestName: String,
        headers: Map<String, String> = mapOf()
    ): Response = makeRequest(
        endpoint = endpoint,
        body = body,
        headers = headers,
        contentType = ContentType.MULTIPART,
        method = HttpMethods.POST,
        fileUpload = FileUpload(
            file = file,
            requestName = requestName
        ),
    )

    suspend fun <T> put(
        endpoint: String = "/",
        body: T,
        headers: Map<String, String> = mapOf()
    ): Response = makeRequest(
        endpoint = endpoint,
        body = body,
        headers = headers,
        contentType = ContentType.JSON,
        method = HttpMethods.PUT,
    )

    suspend fun <T> put(
        endpoint: String = "/",
        body: T,
        file: File?,
        requestName: String,
        headers: Map<String, String> = mapOf()
    ): Response = makeRequest(
        endpoint = endpoint,
        body = body,
        headers = headers,
        contentType = ContentType.MULTIPART,
        method = HttpMethods.PUT,
        fileUpload = FileUpload(
            file = file,
            requestName = requestName
        ),
    )

    suspend fun <T> delete(
        endpoint: String = "/",
        body: T? = null,
        headers: Map<String, String> = mapOf()
    ): Response = makeRequest(
        endpoint = endpoint,
        body = body,
        headers = headers,
        contentType = ContentType.JSON,
        method = HttpMethods.DELETE,
    )

    private suspend fun makeRequest(
        endpoint: String,
        method: HttpMethods,
        body: Any? = null,
        headers: Map<String, String> = mapOf(),
        fileUpload: FileUpload = FileUpload(),
        contentType: ContentType? = null
    ): Response {
        val requestBody = getRequestBody(
            body = body,
            contentType = contentType,
            fileUpload = fileUpload
        )
        val requestBuilder = Request.Builder().url(
            url = "$BASE_URL/$path$endpoint"
        )
        setHeaders(
            requestBuilder = requestBuilder,
            headers = headers
        )
        setHttpMethod(
            requestBody = requestBody,
            method = method,
            requestBuilder = requestBuilder
        )
        val request = requestBuilder.build()
        return okHttpClient.newCall(request = request).await()
    }

    private fun setHttpMethod(
        requestBody: RequestBody?,
        method: HttpMethods,
        requestBuilder: Request.Builder
    ) = requestBody?.let {
        when (method) {
            HttpMethods.POST -> requestBuilder.post(it)
            HttpMethods.PUT -> requestBuilder.put(it)
            HttpMethods.DELETE -> requestBuilder.delete(it)
            else -> null
        }
    } ?: when (method) {
        HttpMethods.GET -> requestBuilder.get()
        HttpMethods.DELETE -> requestBuilder.delete()
        else -> throw IllegalArgumentException(
            "Unable to create a request. Invalid body and method provided"
        )
    }

    private fun getRequestBody(
        body: Any?,
        contentType: ContentType?,
        fileUpload: FileUpload
    ): RequestBody? = body?.let {
        when (contentType) {
            ContentType.JSON -> createJsonRequestBody(it)
            ContentType.MULTIPART -> createMultipartRequestBody(it, fileUpload)
            else -> null
        }
    }

    private fun createJsonRequestBody(body: Any): RequestBody =
        gson.toJson(body).toRequestBody(JSON_MEDIA_TYPE)

    private fun createMultipartRequestBody(
        body: Any,
        fileUpload: FileUpload = FileUpload(),
    ): RequestBody {
        val map = gson.decodeFromJson<HashMap<String, Any>>(
            gson.toJson(body)
        )
        val multipartBuilder = MultipartBody.Builder()
        map.forEach { (key, value) ->
            multipartBuilder.addFormDataPart(key, value.toString())
        }
        fileUpload.file?.let {
            multipartBuilder.addFormDataPart(
                fileUpload.requestName,
                it.name,
                it.asRequestBody(IMAGE_MEDIA_TYPE)
            )
        }
        return multipartBuilder.build()
    }

    private fun setHeaders(
        requestBuilder: Request.Builder,
        headers: Map<String, String> = mapOf()
    ): Request.Builder = requestBuilder.apply {
        headers.forEach { addHeader(it.key, it.value) }
    }

    private data class FileUpload(
        val file: File? = null,
        val requestName: String = "image"
    )

    private enum class HttpMethods {
        GET, POST, PUT, DELETE
    }

    private enum class ContentType {
        JSON, MULTIPART
    }
}