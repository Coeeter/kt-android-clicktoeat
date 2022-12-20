package com.nasportfolio.data.user.local

import com.nasportfolio.domain.utils.Resource

interface SharedPreferenceDao {
    fun saveToken(token: String)
    fun getToken(): Resource<String>
    fun removeToken()
}