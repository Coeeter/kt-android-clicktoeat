package com.nasportfolio.test.restaurant

import com.nasportfolio.domain.image.Image
import com.nasportfolio.domain.restaurant.Restaurant
import com.nasportfolio.domain.restaurant.RestaurantRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError

class FakeRestaurantRepository : RestaurantRepository {
    var restaurants: List<Restaurant> = emptyList()

    init {
        restaurants = restaurants.toMutableList().apply {
            repeat(10) {
                val restaurant = Restaurant(
                    id = it.toString(),
                    name = "name $it",
                    description = "description $it",
                    image = Image(
                        id = it,
                        key = "key $it",
                        url = "https://3.bp.blogspot.com/-VVp3WvJvl84/X0Vu6EjYqDI/AAAAAAAAPjU/ZOMKiUlgfg8ok8DY8Hc-ocOvGdB0z86AgCLcBGAsYHQ/s1600/jetpack%2Bcompose%2Bicon_RGB.png"
                    )
                )
                add(restaurant)
            }
        }
    }

    override suspend fun getAllRestaurants(): Resource<List<Restaurant>> {
        return Resource.Success(restaurants)
    }

    override suspend fun getRestaurantById(id: String): Resource<Restaurant> {
        val restaurant = restaurants.find { it.id == id } ?: return Resource.Failure(
            ResourceError.DefaultError("Cannot find restaurant with id $id")
        )
        return Resource.Success(restaurant)
    }

    override suspend fun createRestaurant(
        token: String,
        name: String,
        description: String,
        image: ByteArray
    ): Resource<String> {
        val restaurant = Restaurant(
            id = restaurants.size.toString(),
            name = name,
            description = description,
            image = Image(
                id = restaurants.size,
                key = "key ${restaurants.size}",
                url = "https://3.bp.blogspot.com/-VVp3WvJvl84/X0Vu6EjYqDI/AAAAAAAAPjU/ZOMKiUlgfg8ok8DY8Hc-ocOvGdB0z86AgCLcBGAsYHQ/s1600/jetpack%2Bcompose%2Bicon_RGB.png",
            )
        )
        restaurants = restaurants.toMutableList().apply {
            add(restaurant)
        }
        return Resource.Success(restaurant.id)
    }

    override suspend fun updateRestaurant(
        token: String,
        restaurantId: String,
        name: String?,
        description: String?,
        image: ByteArray?
    ): Resource<Restaurant> {
        var restaurant = restaurants.find { it.id == restaurantId } ?: return Resource.Failure(
            ResourceError.DefaultError("Cannot find restaurant with id $restaurantId")
        )
        val updatedImage = image?.let {
            val id = restaurant.image.id + 1
            Image(
                id = id,
                key = "key $id",
                url = "https://3.bp.blogspot.com/-VVp3WvJvl84/X0Vu6EjYqDI/AAAAAAAAPjU/ZOMKiUlgfg8ok8DY8Hc-ocOvGdB0z86AgCLcBGAsYHQ/s1600/jetpack%2Bcompose%2Bicon_RGB.png"
            )
        } ?: restaurant.image
        restaurant = restaurant.copy(
            name = name ?: restaurant.name,
            description = description ?: restaurant.description,
            image = updatedImage
        )
        restaurants = restaurants.toMutableList().apply {
            val index = map { it.id }.indexOf(restaurant.id)
            set(index, restaurant)
        }
        return Resource.Success(restaurant)
    }

    override suspend fun deleteRestaurant(
        token: String,
        restaurantId: String
    ): Resource<String> {
        val restaurant = restaurants.find { it.id == restaurantId } ?: return Resource.Failure(
            ResourceError.DefaultError("Cannot find restaurant with id $restaurantId")
        )
        restaurants = restaurants.filter { it.id != restaurant.id }
        return Resource.Success("Deleted restaurant with id $restaurantId")
    }
}