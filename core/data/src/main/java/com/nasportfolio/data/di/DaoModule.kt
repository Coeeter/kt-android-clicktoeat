package com.nasportfolio.data.di

import com.nasportfolio.data.branch.remote.RemoteBranchDao
import com.nasportfolio.data.branch.remote.RemoteBranchDaoImpl
import com.nasportfolio.data.comment.remote.RemoteCommentDao
import com.nasportfolio.data.comment.remote.RemoteCommentDaoImpl
import com.nasportfolio.data.dislike.remote.RemoteDislikeDao
import com.nasportfolio.data.dislike.remote.RemoteDislikeDaoImpl
import com.nasportfolio.data.favorite.remote.RemoteFavoriteDao
import com.nasportfolio.data.favorite.remote.RemoteFavoriteDaoImpl
import com.nasportfolio.data.image.local.LocalImageDao
import com.nasportfolio.data.image.local.LocalImageDaoImpl
import com.nasportfolio.data.image.remote.RemoteImageDao
import com.nasportfolio.data.image.remote.RemoteImageDaoImpl
import com.nasportfolio.data.like.remote.RemoteLikeDao
import com.nasportfolio.data.like.remote.RemoteLikeDaoImpl
import com.nasportfolio.data.restaurant.remote.RemoteRestaurantDao
import com.nasportfolio.data.restaurant.remote.RemoteRestaurantDaoImpl
import com.nasportfolio.data.user.local.SharedPreferenceDao
import com.nasportfolio.data.user.local.SharedPreferenceDaoImpl
import com.nasportfolio.data.user.remote.RemoteUserDao
import com.nasportfolio.data.user.remote.RemoteUserDaoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DaoModule {

    @Binds
    abstract fun bindsRemoteRestaurantDao(
        remoteRestaurantDaoImpl: RemoteRestaurantDaoImpl
    ): RemoteRestaurantDao

    @Binds
    abstract fun bindsRemoteUserDao(
        remoteUserDaoImpl: RemoteUserDaoImpl
    ): RemoteUserDao

    @Binds
    abstract fun bindsSharedPreferenceDao(
        sharedPreferenceDaoImpl: SharedPreferenceDaoImpl
    ): SharedPreferenceDao

    @Binds
    abstract fun bindsRemoteBranchDao(
        remoteBranchDaoImpl: RemoteBranchDaoImpl
    ): RemoteBranchDao

    @Binds
    abstract fun bindsRemoteCommentDao(
        remoteCommentDaoImpl: RemoteCommentDaoImpl
    ): RemoteCommentDao

    @Binds
    abstract fun bindsRemoteFavoriteDao(
        remoteFavoriteDaoImpl: RemoteFavoriteDaoImpl
    ): RemoteFavoriteDao

    @Binds
    abstract fun bindsRemoteLikeDao(
        remoteLikeDaoImpl: RemoteLikeDaoImpl
    ): RemoteLikeDao

    @Binds
    abstract fun bindsRemoteDislikeDao(
        remoteDislikeDaoImpl: RemoteDislikeDaoImpl
    ): RemoteDislikeDao

    @Binds
    abstract fun bindsLocalImageDao(
        localImageDaoImpl: LocalImageDaoImpl
    ): LocalImageDao

    @Binds
    abstract fun bindsRemoteImageDao(
        remoteImageDaoImpl: RemoteImageDaoImpl
    ): RemoteImageDao

}