package com.nasportfolio.clicktoeat.data.common.converter

interface JsonConverter {
    fun <T> toJson(src: T): String
    fun <T> fromJson(json: String): T
}