package com.nasportfolio.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nasportfolio.data.branch.local.BranchEntity
import com.nasportfolio.data.branch.local.LocalBranchDao
import com.nasportfolio.data.restaurant.local.LocalRestaurantDao
import com.nasportfolio.data.restaurant.local.RestaurantEntity

@Database(
    version = 1,
    entities = [BranchEntity::class, RestaurantEntity::class],
)
abstract class CltLocalDatabase : RoomDatabase() {
    abstract fun getBranchDao(): LocalBranchDao
    abstract fun getRestaurantDao(): LocalRestaurantDao
}