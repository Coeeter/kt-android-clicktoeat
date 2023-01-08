package com.nasportfolio.network

import com.google.gson.Gson
import com.nasportfolio.network.models.TransformedResponse

interface OkHttpDao {

    val gson: Gson

    suspend fun get(
        endpoint: String = "/",
        headers: Map<String, String> = mapOf()
    ): TransformedResponse

    suspend fun <T> post(
        endpoint: String = "/",
        body: T,
        headers: Map<String, String> = mapOf()
    ): TransformedResponse

    suspend fun <T> post(
        endpoint: String = "/",
        body: T,
        file: ByteArray?,
        requestName: String,
        headers: Map<String, String> = mapOf()
    ): TransformedResponse

    suspend fun <T> put(
        endpoint: String = "/",
        body: T,
        headers: Map<String, String> = mapOf()
    ): TransformedResponse

    suspend fun <T> put(
        endpoint: String = "/",
        body: T,
        file: ByteArray?,
        requestName: String,
        headers: Map<String, String> = mapOf()
    ): TransformedResponse

    suspend fun <T> delete(
        endpoint: String = "/",
        body: T? = null,
        headers: Map<String, String> = mapOf()
    ): TransformedResponse

}