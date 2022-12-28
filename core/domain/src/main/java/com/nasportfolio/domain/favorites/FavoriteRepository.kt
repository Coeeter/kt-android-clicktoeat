package com.nasportfolio.domain.favorites

import com.nasportfolio.domain.restaurant.Restaurant
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.utils.Resource

interface FavoriteRepository {
    suspend fun getFavoriteRestaurantsOfUser(userId: String): Resource<List<Restaurant>>
    suspend fun getUsersWhoFavoriteRestaurant(restaurantId: String): Resource<List<User>>
    suspend fun addFavorite(token: String, restaurantId: String): Resource<String>
    suspend fun removeFavorite(token: String, restaurantId: String): Resource<String>
}