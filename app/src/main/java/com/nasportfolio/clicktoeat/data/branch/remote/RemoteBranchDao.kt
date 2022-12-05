package com.nasportfolio.clicktoeat.data.branch.remote

import com.google.gson.Gson
import com.nasportfolio.clicktoeat.data.branch.remote.dtos.CreateBranchDto
import com.nasportfolio.clicktoeat.data.common.OkHttpDao
import com.nasportfolio.clicktoeat.domain.branch.Branch
import com.nasportfolio.clicktoeat.domain.utils.Resource
import okhttp3.OkHttpClient

abstract class RemoteBranchDao(
    okHttpClient: OkHttpClient,
    gson: Gson
) : OkHttpDao(okHttpClient, gson, "/api/branches") {
    abstract suspend fun getAllBranches(): Resource<List<Branch>>

    abstract suspend fun createBranch(
        token: String,
        restaurantId: String,
        createBranchDto: CreateBranchDto
    ): Resource<String>

    abstract suspend fun deleteBranch(
        token: String,
        branchId: String,
        restaurantId: String
    ): Resource<String>
}