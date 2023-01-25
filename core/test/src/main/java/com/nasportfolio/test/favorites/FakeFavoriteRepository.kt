package com.nasportfolio.test.favorites

import com.nasportfolio.domain.favorites.FavoriteRepository
import com.nasportfolio.test.restaurant.FakeRestaurantRepository
import com.nasportfolio.domain.restaurant.Restaurant
import com.nasportfolio.test.user.FakeUserRepository
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.utils.Resource

class FakeFavoriteRepository(
    private val fakeUserRepository: FakeUserRepository,
    private val fakeRestaurantRepository: FakeRestaurantRepository
) : FavoriteRepository {
    var favorites: List<Favorite> = emptyList()

    init {
        favorites = favorites.toMutableList().apply {
            repeat(10) {
                val favorite = Favorite(
                    restaurantId = it.toString(),
                    userId = it.toString()
                )
                add(favorite)
            }
        }
    }

    data class Favorite(
        val restaurantId: String,
        val userId: String
    )

    override suspend fun getFavoriteRestaurantsOfUser(userId: String): Resource<List<Restaurant>> {
        return Resource.Success(
            favorites.filter { userId == it.userId }.mapNotNull { favorite ->
                fakeRestaurantRepository.restaurants.find { favorite.restaurantId == it.id }
            }
        )
    }

    override suspend fun getUsersWhoFavoriteRestaurant(restaurantId: String): Resource<List<User>> {
        return Resource.Success(
            favorites.filter { restaurantId == it.restaurantId }.mapNotNull { favorite ->
                fakeUserRepository.users.find { favorite.userId == it.id }
            }
        )
    }

    override suspend fun addFavorite(token: String, restaurantId: String): Resource<String> {
        return when (val account = fakeUserRepository.validateToken(token)) {
            is Resource.Success -> {
                favorites = favorites.toMutableList().apply {
                    val favorite = Favorite(
                        userId = account.result.id,
                        restaurantId = restaurantId
                    )
                    add(favorite)
                }
                return Resource.Success("Added favorite")
            }
            is Resource.Failure -> Resource.Failure(account.error)
            else -> throw IllegalStateException()
        }
    }

    override suspend fun removeFavorite(token: String, restaurantId: String): Resource<String> {
        return when (val account = fakeUserRepository.validateToken(token)) {
            is Resource.Success -> {
                favorites = favorites.filter {
                    it.userId != account.result.id && it.restaurantId != restaurantId
                }
                return Resource.Success("Removed favorite")
            }
            is Resource.Failure -> Resource.Failure(account.error)
            else -> throw IllegalStateException()
        }
    }
}