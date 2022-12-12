package com.nasportfolio.data.user

import com.nasportfolio.data.user.local.SharedPreferenceDao
import com.nasportfolio.data.user.remote.RemoteUserDao
import com.nasportfolio.domain.user.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remoteUserDao: RemoteUserDao,
    private val sharedPreferenceDao: SharedPreferenceDao
) : UserRepository {

}