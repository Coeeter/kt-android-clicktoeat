package com.nasportfolio.data.branch.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalBranchDao {
    @Transaction
    @Query("SELECT * FROM branch")
    fun getAllBranchesWithRestaurant(): Flow<List<BranchWithRestaurant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranch(vararg branchEntity: BranchEntity)

    @Query("DELETE FROM branch WHERE branchId = :branchId")
    suspend fun deleteBranchById(branchId: String)

    @Query("DELETE FROM branch")
    suspend fun deleteAllBranches()
}