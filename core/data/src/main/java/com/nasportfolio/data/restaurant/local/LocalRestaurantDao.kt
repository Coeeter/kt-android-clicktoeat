package com.nasportfolio.data.restaurant.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalRestaurantDao {
    @Transaction
    @Query("SELECT * FROM restaurant")
    fun getRestaurantsWithBranches(): Flow<List<RestaurantWithBranches>>

    @Transaction
    @Query("SELECT * FROM restaurant WHERE restaurantId = :restaurantId")
    fun getRestaurantById(restaurantId: String): Flow<RestaurantWithBranches>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurants(vararg restaurantEntity: RestaurantEntity)

    @Query("DELETE FROM restaurant WHERE restaurantId = :restaurantId")
    suspend fun deleteRestaurantById(restaurantId: String)

    @Query("DELETE FROM restaurant")
    suspend fun deleteAllRestaurants()
}