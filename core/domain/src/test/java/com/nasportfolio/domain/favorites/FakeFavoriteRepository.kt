package com.nasportfolio.domain.favorites

import com.nasportfolio.domain.restaurant.Restaurant
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.utils.Resource

class FakeFavoriteRepository: FavoriteRepository {
    var favoriteRestaurants: List<String> = emptyList()

    override suspend fun getFavoriteRestaurantsOfUser(userId: String): Resource<List<Restaurant>> {
        return Resource.Success(listOf())
    }

    override suspend fun getUsersWhoFavoriteRestaurant(restaurantId: String): Resource<List<User>> {
        return Resource.Success(listOf())
    }

    override suspend fun addFavorite(token: String, restaurantId: String): Resource<String> {
        favoriteRestaurants = favoriteRestaurants.toMutableList().apply {
            add(restaurantId)
        }
        return Resource.Success("Added fav")
    }

    override suspend fun removeFavorite(token: String, restaurantId: String): Resource<String> {
        favoriteRestaurants = favoriteRestaurants.toMutableList().apply {
            val index = indexOf(restaurantId)
            removeAt(index)
        }
        return Resource.Success("Removed fav")
    }
}