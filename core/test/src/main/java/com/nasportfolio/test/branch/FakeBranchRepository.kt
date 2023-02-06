package com.nasportfolio.test.branch

import com.nasportfolio.domain.branch.Branch
import com.nasportfolio.domain.branch.BranchRepository
import com.nasportfolio.domain.image.Image
import com.nasportfolio.domain.restaurant.Restaurant
import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeBranchRepository : BranchRepository {
    var branches: List<Branch> = emptyList()

    init {
        branches = branches.toMutableList().apply {
            repeat(10) {
                val branch = Branch(
                    id = it.toString(),
                    latitude = 0.0,
                    longitude = 0.0,
                    address = "address $it",
                    restaurant = createRestaurant(it.toString()),
                )
                add(branch)
            }
        }
    }

    private fun createRestaurant(index: String) = Restaurant(
        id = index,
        name = "name $index",
        description = "description $index",
        image = Image(
            id = 0,
            key = "key $index",
            url = "https://3.bp.blogspot.com/-VVp3WvJvl84/X0Vu6EjYqDI/AAAAAAAAPjU/ZOMKiUlgfg8ok8DY8Hc-ocOvGdB0z86AgCLcBGAsYHQ/s1600/jetpack%2Bcompose%2Bicon_RGB.png"
        )
    )

    override fun getAllBranches(fetchFromRemote: Boolean): Flow<Resource<List<Branch>>> = flow {
        emit(Resource.Success(branches))
    }

    override suspend fun createBranch(
        token: String,
        restaurantId: String,
        address: String,
        latitude: Double,
        longitude: Double
    ): Resource<String> {
        branches = branches.toMutableList().apply {
            val branch = Branch(
                id = branches.size.toString(),
                address = address,
                latitude = latitude,
                longitude = longitude,
                restaurant = createRestaurant(restaurantId)
            )
            add(branch)
        }
        return Resource.Success(branches.last().id)
    }

    override suspend fun deleteBranch(
        token: String,
        branchId: String,
        restaurantId: String
    ): Resource<String> {
        branches = branches.filter { it.id != branchId }
        return Resource.Success("Removed branch")
    }
}