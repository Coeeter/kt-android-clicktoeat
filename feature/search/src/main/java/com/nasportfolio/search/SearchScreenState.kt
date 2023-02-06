package com.nasportfolio.search

import com.nasportfolio.domain.restaurant.TransformedRestaurant
import com.nasportfolio.domain.user.User

data class SearchScreenState(
    val query: String = "",
    val isRestaurantLoading: Boolean = true,
    val isUserLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val restaurants: List<TransformedRestaurant> = emptyList(),
    val users: List<User> = emptyList(),
    val currentLoggedInUser: User? = null,
) {
    val filteredRestaurants: List<TransformedRestaurant>
        get() {
            if (query.isEmpty()) return restaurants
            return restaurants.filter { it.name.contains(query, ignoreCase = true) }
                .sortedByDescending { it.ratingCount }
        }

    val filteredUsers: List<User>
        get() {
            if (query.isEmpty()) return users
            return users.filter { it.username.contains(query, ignoreCase = true) }
        }
}
