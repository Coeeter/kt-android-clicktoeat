package com.nasportfolio.clicktoeat.data.user

import com.nasportfolio.clicktoeat.data.user.local.SharedPreferenceDao
import com.nasportfolio.clicktoeat.data.user.remote.RemoteUserDao
import com.nasportfolio.clicktoeat.domain.user.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remoteUserDao: RemoteUserDao,
    private val sharedPreferenceDao: SharedPreferenceDao
) : UserRepository {

}