package com.nasportfolio.clicktoeat.data.branch.remote

import com.nasportfolio.clicktoeat.data.branch.remote.dtos.CreateBranchDto
import com.nasportfolio.clicktoeat.domain.branch.Branch
import com.nasportfolio.clicktoeat.domain.utils.Resource

interface RemoteBranchDao {
    suspend fun getAllBranches(): Resource<List<Branch>>

    suspend fun createBranch(
        token: String,
        restaurantId: String,
        createBranchDto: CreateBranchDto
    ): Resource<String>

    suspend fun deleteBranch(
        token: String,
        branchId: String,
        restaurantId: String
    ): Resource<String>
}