package com.nasportfolio.data.branch

import com.nasportfolio.data.CltLocalDatabase
import com.nasportfolio.data.branch.local.BranchEntity
import com.nasportfolio.data.branch.local.toBranchEntity
import com.nasportfolio.data.branch.local.toExternalBranch
import com.nasportfolio.data.branch.remote.RemoteBranchDao
import com.nasportfolio.data.branch.remote.dtos.CreateBranchDto
import com.nasportfolio.domain.branch.Branch
import com.nasportfolio.domain.branch.BranchRepository
import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class BranchRepositoryImpl @Inject constructor(
    private val remoteBranchDao: RemoteBranchDao,
    cltLocalDatabase: CltLocalDatabase,
) : BranchRepository {
    private val localBranchDao = cltLocalDatabase.getBranchDao()

    override fun getAllBranches(
        fetchFromRemote: Boolean
    ): Flow<Resource<List<Branch>>> = flow {
        val localBranches = localBranchDao.getAllBranchesWithRestaurant().first()
        if (localBranches.isNotEmpty() && !fetchFromRemote) return@flow emitAll(
            localBranchDao.getAllBranchesWithRestaurant()
                .map { list -> list.mapNotNull { it.toExternalBranch() } }
                .map { list -> Resource.Success(list) }
                .conflate()
        )
        val branches = remoteBranchDao.getAllBranches()
        if (branches !is Resource.Success) return@flow emit(branches)
        val branchEntityList = branches.result.map { it.toBranchEntity() }
        localBranchDao.deleteAllBranches()
        localBranchDao.insertBranch(*branchEntityList.toTypedArray())
        emitAll(
            localBranchDao.getAllBranchesWithRestaurant()
                .map { list -> list.mapNotNull { it.toExternalBranch() } }
                .map { list -> Resource.Success(list) }
                .conflate()
        )
    }


    override suspend fun createBranch(
        token: String,
        restaurantId: String,
        address: String,
        latitude: Double,
        longitude: Double
    ): Resource<String> {
        val createResult = remoteBranchDao.createBranch(
            token = token,
            restaurantId = restaurantId,
            createBranchDto = CreateBranchDto(
                address = address,
                latitude = latitude,
                longitude = longitude
            )
        )
        if (createResult !is Resource.Success) return createResult
        val branchEntity = BranchEntity(
            branchId = createResult.result,
            address = address,
            latitude = latitude,
            longitude = longitude,
            restaurantId = restaurantId
        )
        localBranchDao.insertBranch(branchEntity)
        return Resource.Success(branchEntity.branchId)
    }

    override suspend fun deleteBranch(
        token: String,
        branchId: String,
        restaurantId: String
    ): Resource<String> {
        val deleteResult = remoteBranchDao.deleteBranch(
            token = token,
            branchId = branchId,
            restaurantId = restaurantId
        )
        if (deleteResult !is Resource.Success) return deleteResult
        localBranchDao.deleteBranchById(branchId = branchId)
        return deleteResult
    }

}