package com.nasportfolio.network

interface Authorization {
    companion object {
        const val AUTHORIZATION = "Authorization"
        const val BEARER = "Bearer"
    }

    fun createAuthorizationHeader(token: String): Map<String, String>
}