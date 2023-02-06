package com.nasportfolio.data.branch.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BranchDao {
    @Transaction
    @Query("SELECT * FROM branch")
    suspend fun getAllBranchesWithRestaurant(): Flow<List<BranchWithRestaurant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranch(vararg branchEntity: BranchEntity)

    @Query("DELETE FROM branch")
    suspend fun deleteAllBranches()
}