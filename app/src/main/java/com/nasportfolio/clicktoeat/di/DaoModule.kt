package com.nasportfolio.clicktoeat.di

import com.nasportfolio.clicktoeat.data.branch.remote.RemoteBranchDao
import com.nasportfolio.clicktoeat.data.branch.remote.RemoteBranchDaoImpl
import com.nasportfolio.clicktoeat.data.comment.remote.RemoteCommentDao
import com.nasportfolio.clicktoeat.data.comment.remote.RemoteCommentDaoImpl
import com.nasportfolio.clicktoeat.data.dislike.remote.RemoteDislikeDao
import com.nasportfolio.clicktoeat.data.dislike.remote.RemoteDislikeDaoImpl
import com.nasportfolio.clicktoeat.data.favorite.remote.RemoteFavoriteDao
import com.nasportfolio.clicktoeat.data.favorite.remote.RemoteFavoriteDaoImpl
import com.nasportfolio.clicktoeat.data.like.remote.RemoteLikeDao
import com.nasportfolio.clicktoeat.data.like.remote.RemoteLikeDaoImpl
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

}