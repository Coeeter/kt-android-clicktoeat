package com.nasportfolio.clicktoeat.di

import com.nasportfolio.clicktoeat.data.branch.remote.RemoteBranchDao
import com.nasportfolio.clicktoeat.data.branch.remote.RemoteBranchDaoImpl
import com.nasportfolio.clicktoeat.data.comment.remote.RemoteCommentDao
import com.nasportfolio.clicktoeat.data.comment.remote.RemoteCommentDaoImpl
import com.nasportfolio.clicktoeat.data.restaurant.remote.RemoteRestaurantDao
import com.nasportfolio.clicktoeat.data.restaurant.remote.RemoteRestaurantDaoImpl
import com.nasportfolio.clicktoeat.data.user.local.SharedPreferenceDao
import com.nasportfolio.clicktoeat.data.user.local.SharedPreferenceDaoImpl
import com.nasportfolio.clicktoeat.data.user.remote.RemoteUserDao
import com.nasportfolio.clicktoeat.data.user.remote.RemoteUserDaoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DaoModule {

    @Singleton
    @Binds
    abstract fun bindsRemoteRestaurantDao(
        remoteRestaurantDaoImpl: RemoteRestaurantDaoImpl
    ): RemoteRestaurantDao

    @Singleton
    @Binds
    abstract fun bindsRemoteUserDao(
        remoteUserDaoImpl: RemoteUserDaoImpl
    ): RemoteUserDao

    @Binds
    abstract fun bindsSharedPreferenceDao(
        sharedPreferenceDaoImpl: SharedPreferenceDaoImpl
    ): SharedPreferenceDao

    @Singleton
    @Binds
    abstract fun bindsRemoteBranchDao(
        remoteBranchDaoImpl: RemoteBranchDaoImpl
    ): RemoteBranchDao

    @Singleton
    @Binds
    abstract fun bindsRemoteCommentDao(
        remoteCommentDaoImpl: RemoteCommentDaoImpl
    ): RemoteCommentDao

}