package com.nasportfolio.data.branch

import com.nasportfolio.data.branch.remote.RemoteBranchDao
import com.nasportfolio.data.branch.remote.dtos.CreateBranchDto
import com.nasportfolio.domain.branch.Branch
import com.nasportfolio.domain.branch.BranchRepository
import com.nasportfolio.domain.utils.Resource
import javax.inject.Inject

class BranchRepositoryImpl @Inject constructor(
    private val remoteBranchDao: RemoteBranchDao
) : BranchRepository {

    override suspend fun getAllBranches(): Resource<List<Branch>> =
        remoteBranchDao.getAllBranches()

    override suspend fun createBranch(
        token: String,
        restaurantId: String,
        address: String,
        latitude: Double,
        longitude: Double
    ): Resource<String> = remoteBranchDao.createBranch(
        token = token,
        restaurantId = restaurantId,
        createBranchDto = CreateBranchDto(
            address = address,
            latitude = latitude,
            longitude = longitude
        )
    )

    override suspend fun deleteBranch(
        token: String,
        branchId: String,
        restaurantId: String
    ): Resource<String> = remoteBranchDao.deleteBranch(
        token = token,
        branchId = branchId,
        restaurantId = restaurantId
    )

}