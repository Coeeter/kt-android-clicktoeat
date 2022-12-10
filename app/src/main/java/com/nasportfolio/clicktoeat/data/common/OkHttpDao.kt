package com.nasportfolio.clicktoeat.data.common

import com.nasportfolio.clicktoeat.data.common.converter.JsonConverter
import okhttp3.Response
import java.io.File

interface OkHttpDao {

    val converter: JsonConverter

    suspend fun get(
        endpoint: String = "/",
        headers:
        Map<String, String> = mapOf()
    ): Response

    suspend fun <T> post(
        endpoint: String = "/",
        body: T,
        headers: Map<String, String> = mapOf()
    ): Response

    suspend fun <T> post(
        endpoint: String = "/",
        body: T,
        file: File?,
        requestName: String,
        headers: Map<String, String> = mapOf()
    ): Response

    suspend fun <T> put(
        endpoint: String = "/",
        body: T,
        headers: Map<String, String> = mapOf()
    ): Response

    suspend fun <T> put(
        endpoint: String = "/",
        body: T,
        file: File?,
        requestName: String,
        headers: Map<String, String> = mapOf()
    ): Response

    suspend fun <T> delete(
        endpoint: String = "/",
        body: T? = null,
        headers: Map<String, String> = mapOf()
    ): Response

}