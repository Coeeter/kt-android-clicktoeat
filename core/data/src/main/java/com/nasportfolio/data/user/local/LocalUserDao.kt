package com.nasportfolio.data.user.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalUserDao {
    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(vararg user: UserEntity)

    @Query("DELETE FROM user WHERE userId = :id")
    suspend fun deleteUserById(id: String)

    @Query("DELETE FROM user")
    suspend fun deleteAllUsers()
}