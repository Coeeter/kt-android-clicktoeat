package com.nasportfolio.clicktoeat.data.common.delegations

import com.nasportfolio.clicktoeat.data.common.Authorization

class AuthorizationImpl : Authorization {
    override fun createAuthorizationHeader(token: String) = mapOf(
        Authorization.AUTHORIZATION to "${Authorization.BEARER} $token"
    )
}