package com.nasportfolio.clicktoeat.data.favorite.remote

import com.nasportfolio.clicktoeat.domain.restaurant.Restaurant
import com.nasportfolio.clicktoeat.domain.user.User
import com.nasportfolio.clicktoeat.domain.utils.Resource

interface RemoteFavoriteDao {
    suspend fun getFavoriteRestaurantsOfUser(userId: String): Resource<List<Restaurant>>
    suspend fun getUsersWhoFavoriteRestaurant(restaurantId: String): Resource<List<User>>
    suspend fun addFavorite(token: String, restaurantId: String): Resource<String>
    suspend fun removeFavorite(token: String, restaurantId: String): Resource<String>
}