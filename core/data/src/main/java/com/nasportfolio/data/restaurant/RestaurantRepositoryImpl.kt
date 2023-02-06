package com.nasportfolio.data.restaurant

import android.util.Log
import androidx.room.withTransaction
import com.nasportfolio.data.CltLocalDatabase
import com.nasportfolio.data.restaurant.local.toExternalRestaurant
import com.nasportfolio.data.restaurant.local.toRestaurantEntity
import com.nasportfolio.data.restaurant.remote.RemoteRestaurantDao
import com.nasportfolio.data.restaurant.remote.dtos.CreateRestaurantDto
import com.nasportfolio.data.restaurant.remote.dtos.UpdateRestaurantDto
import com.nasportfolio.domain.restaurant.Restaurant
import com.nasportfolio.domain.restaurant.RestaurantRepository
import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject constructor(
    private val remoteRestaurantDao: RemoteRestaurantDao,
    private val cltLocalDatabase: CltLocalDatabase
) : RestaurantRepository {
    private val localRestaurantDao = cltLocalDatabase.getRestaurantDao()

    override fun getAllRestaurants(fetchFromRemote: Boolean): Flow<Resource<List<Restaurant>>> =
        localRestaurantDao.getRestaurantsWithBranches()
            .map { list -> list.map { it.toExternalRestaurant() } }
            .map { list ->
                if (list.isNotEmpty() && !fetchFromRemote) return@map Resource.Success(
                    list.mapNotNull { it }
                )
                val restaurants = remoteRestaurantDao.getAllRestaurants()
                if (restaurants !is Resource.Success) return@map restaurants
                val restaurantEntityList = restaurants.result.map { it.toRestaurantEntity() }
                localRestaurantDao.deleteAllRestaurants()
                localRestaurantDao.insertRestaurants(*restaurantEntityList.toTypedArray())
                restaurants
            }


    override fun getRestaurantById(id: String): Flow<Resource<Restaurant>> {
        return localRestaurantDao.getRestaurantById(restaurantId = id)
            .mapNotNull { it.toExternalRestaurant() }
            .map { Resource.Success(it) }
    }

    override suspend fun createRestaurant(
        token: String,
        name: String,
        description: String,
        image: ByteArray
    ): Resource<String> {
        val createResult = remoteRestaurantDao.createRestaurant(
            token = token,
            createRestaurantDto = CreateRestaurantDto(
                name = name,
                description = description,
                image = image
            )
        )
        if (createResult !is Resource.Success) return createResult
        val insertId = createResult.result
        val restaurant = remoteRestaurantDao.getRestaurantById(insertId)
        if (restaurant !is Resource.Success) return Resource.Failure(
            (restaurant as Resource.Failure).error
        )
        localRestaurantDao.insertRestaurants(restaurant.result.toRestaurantEntity())
        return Resource.Success(insertId)
    }

    override suspend fun updateRestaurant(
        token: String,
        restaurantId: String,
        name: String?,
        description: String?,
        image: ByteArray?
    ): Resource<Restaurant> {
        val updateResult = remoteRestaurantDao.updateRestaurant(
            token = token,
            id = restaurantId,
            updateRestaurantDto = UpdateRestaurantDto(
                name = name,
                description = description,
                image = image,
            )
        )
        if (updateResult !is Resource.Success) return updateResult
        localRestaurantDao.insertRestaurants(updateResult.result.toRestaurantEntity())
        return updateResult
    }

    override suspend fun deleteRestaurant(
        token: String,
        restaurantId: String
    ): Resource<String> {
        val deleteResult = remoteRestaurantDao.deleteRestaurant(
            token = token,
            id = restaurantId
        )
        if (deleteResult !is Resource.Success) return deleteResult
        localRestaurantDao.deleteRestaurantById(restaurantId)
        return deleteResult
    }
}