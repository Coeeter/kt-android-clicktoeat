package com.nasportfolio.clicktoeat.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun <T> Gson.decodeFromJson(json: String): T = fromJson<T>(
    json,
    object : TypeToken<T>() {}.type
)
