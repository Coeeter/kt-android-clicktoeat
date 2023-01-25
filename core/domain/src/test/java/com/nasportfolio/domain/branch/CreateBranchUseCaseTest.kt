package com.nasportfolio.domain.branch

import com.nasportfolio.domain.branch.usecases.CreateBranchUseCase
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.test.branch.FakeBranchRepository
import com.nasportfolio.test.user.FakeUserRepository
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CreateBranchUseCaseTest {

    private lateinit var fakeBranchRepository: FakeBranchRepository
    private lateinit var createBranchUseCase: CreateBranchUseCase

    @Before
    fun setUp() {
        fakeBranchRepository = FakeBranchRepository()
        createBranchUseCase = CreateBranchUseCase(
            userRepository = FakeUserRepository(),
            branchRepository = fakeBranchRepository
        )
    }

    @Test
    fun `When given invalid inputs should return failure resource`() = runBlocking {
        val result = createBranchUseCase(
            restaurantId = "100",
            address = "",
            latitude = null,
            longitude = null,
        ).last()
        assertTrue(result is Resource.Failure)
    }

    @Test
    fun `When given valid inputs should return success resource and update list`() = runBlocking {
        val result = createBranchUseCase(
            restaurantId = "100",
            address = "test",
            latitude = 30.0,
            longitude = 100.0,
        ).toList()
        assertTrue(result[0] is Resource.Loading)
        assertTrue(result.last() is Resource.Success)
        val insertedBranch = fakeBranchRepository.branches.last()
        assertNotNull(insertedBranch)
        assertEquals("test", insertedBranch.address)
        assertEquals(30.0, insertedBranch.latitude, 0.0)
        assertEquals(100.0, insertedBranch.longitude, 0.0)
        assertEquals("100", insertedBranch.restaurant.id)
    }
}