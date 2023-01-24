package com.nasportfolio.domain.branch.usecases

import com.nasportfolio.domain.branch.FakeBranchRepository
import com.nasportfolio.domain.user.FakeUserRepository
import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeleteBranchUseCaseTest {

    private lateinit var deleteBranchUseCase: DeleteBranchUseCase
    private lateinit var fakeBranchRepository: FakeBranchRepository

    @Before
    fun setUp() {
        fakeBranchRepository = FakeBranchRepository()
        deleteBranchUseCase = DeleteBranchUseCase(
            branchRepository = fakeBranchRepository,
            userRepository = FakeUserRepository()
        )
    }

    @Test
    fun `When given valid inputs should return success resource`() = runBlocking {
        val branchToDelete = fakeBranchRepository.branches.last()
        val result = deleteBranchUseCase(
            branchId = branchToDelete.id,
            restaurantId = branchToDelete.restaurant.id
        ).toList()
        assertTrue(result[0] is Resource.Loading)
        assertTrue(result.last() is Resource.Success)
        assertNull(fakeBranchRepository.branches.find { it.id == branchToDelete.id })
    }
}