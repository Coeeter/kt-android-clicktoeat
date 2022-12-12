package com.nasportfolio.domain.branch

import com.nasportfolio.domain.utils.Resource

interface BranchRepository {

    suspend fun getAllBranches(): Resource<List<Branch>>

    suspend fun createBranch(
        token: String,
        restaurantId: String,
        address: String,
        latitude: Double,
        longitude: Double
    ): Resource<String>

    suspend fun deleteBranch(
        token: String,
        branchId: String,
        restaurantId: String
    ): Resource<String>

}