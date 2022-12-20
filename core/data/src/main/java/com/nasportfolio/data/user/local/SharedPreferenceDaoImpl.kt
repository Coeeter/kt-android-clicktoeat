package com.nasportfolio.data.user.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreferenceDaoImpl @Inject constructor(
    @ApplicationContext context: Context
) : SharedPreferenceDao {
    private val sharedPreferences = context.getSharedPreferences(
        SHARED_PREF_NAME,
        MODE_PRIVATE
    )

    override fun saveToken(token: String) {
        sharedPreferences.edit()
            .putString(TOKEN, token)
            .apply()
    }

    override fun getToken(): Resource<String> {
        val token = sharedPreferences.getString(TOKEN, null)
            ?: return Resource.Failure(
                ResourceError.DefaultError("Not logged in. Login to continue")
            )
        return Resource.Success(token)
    }

    override fun removeToken() {
        sharedPreferences.edit()
            .remove(TOKEN)
            .apply()
    }

    companion object {
        const val SHARED_PREF_NAME = "CLICKTOEAT_PREFS"
        const val TOKEN = "CLICKTOEAT_TOKEN"
    }
}