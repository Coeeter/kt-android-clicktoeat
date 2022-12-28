package com.nasportfolio.data.favorite

import com.nasportfolio.data.favorite.remote.RemoteFavoriteDao
import com.nasportfolio.domain.favorites.FavoriteRepository
import com.nasportfolio.domain.restaurant.Restaurant
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.utils.Resource
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val remoteFavoriteDao: RemoteFavoriteDao
) : FavoriteRepository {
    override suspend fun getFavoriteRestaurantsOfUser(
        userId: String
    ): Resource<List<Restaurant>> = remoteFavoriteDao.getFavoriteRestaurantsOfUser(
        userId = userId
    )

    override suspend fun getUsersWhoFavoriteRestaurant(
        restaurantId: String
    ): Resource<List<User>> = remoteFavoriteDao.getUsersWhoFavoriteRestaurant(
        restaurantId = restaurantId
    )

    override suspend fun addFavorite(
        token: String,
        restaurantId: String
    ): Resource<String> = remoteFavoriteDao.addFavorite(
        token = token,
        restaurantId = restaurantId
    )

    override suspend fun removeFavorite(
        token: String,
        restaurantId: String
    ): Resource<String> = remoteFavoriteDao.removeFavorite(
        token = token,
        restaurantId = restaurantId
    )
}