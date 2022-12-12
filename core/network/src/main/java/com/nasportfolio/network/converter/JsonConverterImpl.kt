package com.nasportfolio.network.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nasportfolio.network.JsonConverter

class JsonConverterImpl(
    private val gson: Gson
) : JsonConverter {
    override fun <T> toJson(src: T): String {
        return gson.toJson(src)
    }

    override fun <T> fromJson(json: String): T {
        return gson.fromJson(json, object : TypeToken<T>() {}.type)
    }
}