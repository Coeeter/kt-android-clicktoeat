package com.nasportfolio.network

interface JsonConverter {
    fun <T> toJson(src: T): String
    fun <T> fromJson(json: String): T
}