package com.nasportfolio.network.delegations

import com.nasportfolio.network.Authorization

class AuthorizationImpl : Authorization {
    override fun createAuthorizationHeader(token: String) = mapOf(
        Authorization.AUTHORIZATION to "${Authorization.BEARER} $token"
    )
}