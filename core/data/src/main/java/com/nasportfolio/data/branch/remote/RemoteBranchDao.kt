package com.nasportfolio.data.branch.remote

import com.nasportfolio.data.branch.remote.dtos.CreateBranchDto
import com.nasportfolio.domain.branch.Branch
import com.nasportfolio.domain.utils.Resource

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