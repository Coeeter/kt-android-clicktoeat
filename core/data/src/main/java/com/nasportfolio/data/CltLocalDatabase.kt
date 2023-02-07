package com.nasportfolio.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nasportfolio.data.branch.local.BranchEntity
import com.nasportfolio.data.branch.local.LocalBranchDao
import com.nasportfolio.data.restaurant.local.LocalRestaurantDao
import com.nasportfolio.data.restaurant.local.RestaurantEntity
import com.nasportfolio.data.user.local.LocalUserDao
import com.nasportfolio.data.user.local.UserEntity

@Database(
    version = 1,
    entities = [
        BranchEntity::class,
        RestaurantEntity::class,
        UserEntity::class
    ],
)
abstract class CltLocalDatabase : RoomDatabase() {
    abstract fun getBranchDao(): LocalBranchDao
    abstract fun getRestaurantDao(): LocalRestaurantDao
    abstract fun getUserDao(): LocalUserDao
}