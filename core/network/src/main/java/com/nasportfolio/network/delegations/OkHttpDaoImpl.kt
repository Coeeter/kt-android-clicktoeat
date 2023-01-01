package com.nasportfolio.network.delegations

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nasportfolio.network.OkHttpDao
import com.nasportfolio.network.mappers.toTransformedResponse
import com.nasportfolio.network.models.TransformedResponse
import com.nasportfolio.network.utils.Constants.BASE_URL
import com.nasportfolio.network.utils.await
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*
import kotlin.collections.HashMap

class OkHttpDaoImpl(
    override val gson: Gson,
    private val okHttpClient: OkHttpClient,
    private val path: String
) : OkHttpDao {

    companion object {
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaTypeOrNull()
        val IMAGE_MEDIA_TYPE = "image/*".toMediaTypeOrNull()
    }

    override suspend fun get(
        endpoint: String,
        headers: Map<String, String>
    ): TransformedResponse = makeRequest(
        endpoint = endpoint,
        headers = headers,
        method = HttpMethods.GET,
    )

    override suspend fun <T> post(
        endpoint: String,
        body: T,
        headers: Map<String, String>
    ): TransformedResponse = makeRequest(
        endpoint = endpoint,
        body = body,
        headers = headers,
        contentType = ContentType.JSON,
        method = HttpMethods.POST
    )

    override suspend fun <T> post(
        endpoint: String,
        body: T,
        file: ByteArray?,
        requestName: String,
        headers: Map<String, String>
    ): TransformedResponse = makeRequest(
        endpoint = endpoint,
        body = body,
        headers = headers,
        contentType = ContentType.MULTIPART,
        method = HttpMethods.POST,
        fileUpload = FileUpload(
            byteArray = file,
            requestName = requestName
        ),
    )

    override suspend fun <T> put(
        endpoint: String,
        body: T,
        headers: Map<String, String>
    ): TransformedResponse = makeRequest(
        endpoint = endpoint,
        body = body,
        headers = headers,
        contentType = ContentType.JSON,
        method = HttpMethods.PUT,
    )

    override suspend fun <T> put(
        endpoint: String,
        body: T,
        file: ByteArray?,
        requestName: String,
        headers: Map<String, String>
    ): TransformedResponse = makeRequest(
        endpoint = endpoint,
        body = body,
        headers = headers,
        contentType = ContentType.MULTIPART,
        method = HttpMethods.PUT,
        fileUpload = FileUpload(
            byteArray = file,
            requestName = requestName
        ),
    )

    override suspend fun <T> delete(
        endpoint: String,
        body: T?,
        headers: Map<String, String>
    ): TransformedResponse = makeRequest(
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
    ): TransformedResponse {
        val requestBody = getRequestBody(
            body = body,
            contentType = contentType,
            fileUpload = fileUpload
        )
        val requestBuilder = Request.Builder().url(
            url = "${BASE_URL}$path$endpoint"
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
        return okHttpClient.newCall(request = request)
            .await()
            .toTransformedResponse()
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
        val map = gson.fromJson<HashMap<String, Any>>(
            gson.toJson(body),
            object : TypeToken<HashMap<String, Any>>() {}.type
        )
        val multipartBuilder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
        map.forEach { (key, value) ->
            multipartBuilder.addFormDataPart(key, value.toString())
        }
        fileUpload.byteArray?.let {
            multipartBuilder.addFormDataPart(
                fileUpload.requestName,
                UUID.randomUUID().toString(),
                it.toRequestBody(IMAGE_MEDIA_TYPE, 0, it.size)
            )
        }
        return multipartBuilder.build()
    }

    private fun setHeaders(
        requestBuilder: Request.Builder,
        headers: Map<String, String> = mapOf()
    ): Request.Builder = requestBuilder.apply {
        headers.forEach { (key, value) ->
            addHeader(key, value)
        }
    }

    private data class FileUpload(
        val byteArray: ByteArray? = null,
        val requestName: String = "image"
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as FileUpload

            if (byteArray != null) {
                if (other.byteArray == null) return false
                if (!byteArray.contentEquals(other.byteArray)) return false
            } else if (other.byteArray != null) return false
            if (requestName != other.requestName) return false

            return true
        }

        override fun hashCode(): Int {
            var result = byteArray?.contentHashCode() ?: 0
            result = 31 * result + requestName.hashCode()
            return result
        }
    }

    private enum class HttpMethods {
        GET, POST, PUT, DELETE
    }

    private enum class ContentType {
        JSON, MULTIPART
    }

}