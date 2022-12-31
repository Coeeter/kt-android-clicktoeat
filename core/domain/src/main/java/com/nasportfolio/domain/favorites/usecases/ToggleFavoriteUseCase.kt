package com.nasportfolio.domain.favorites.usecases

import com.nasportfolio.domain.favorites.FavoriteRepository
import com.nasportfolio.domain.restaurant.TransformedRestaurant
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import javax.inject.Inject
import kotlin.reflect.KSuspendFunction2

class ToggleFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(restaurant: TransformedRestaurant): Resource<Unit> {
        val tokenResource = userRepository.getToken()
        if (tokenResource !is Resource.Success) return Resource.Failure<Unit>(
            (tokenResource as Resource.Failure).error
        )
        val token = tokenResource.result
        val resource = toggleFavorite(restaurant.isFavoriteByCurrentUser).invoke(
            token,
            restaurant.id
        )
        if (resource !is Resource.Success) return Resource.Failure<Unit>(
            (resource as Resource.Failure).error
        )
        return Resource.Success(Unit)
    }

    private fun toggleFavorite(initiallyIsFavorited: Boolean): KSuspendFunction2<String, String, Resource<String>> {
        if (initiallyIsFavorited) return favoriteRepository::removeFavorite
        return favoriteRepository::addFavorite
    }
}