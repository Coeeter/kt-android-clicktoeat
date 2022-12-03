package com.nasportfolio.clicktoeat.data.user.local

import com.nasportfolio.clicktoeat.domain.utils.Resource

interface SharedPreferenceDao {
    fun saveToken(token: String)
    fun getToken(): Resource<String>
}