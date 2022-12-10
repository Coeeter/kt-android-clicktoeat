package com.nasportfolio.clicktoeat.data.common

interface Authorization {
    companion object {
        const val AUTHORIZATION = "Authorization"
        const val BEARER = "Bearer"
    }

    fun createAuthorizationHeader(token: String): Map<String, String>
}