package com.nasportfolio.domain.restaurant.usecases

import com.nasportfolio.domain.restaurant.FakeRestaurantRepository
import com.nasportfolio.domain.user.FakeUserRepository
import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeleteRestaurantUseCaseTest {

    private lateinit var fakeRestaurantRepository: FakeRestaurantRepository
    private lateinit var deleteRestaurantUseCase: DeleteRestaurantUseCase

    @Before
    fun setUp() {
        fakeRestaurantRepository = FakeRestaurantRepository()
        deleteRestaurantUseCase = DeleteRestaurantUseCase(
            restaurantRepository = fakeRestaurantRepository,
            userRepository = FakeUserRepository()
        )
    }

    @Test
    fun `When executed with valid id, should delete restaurant and return success resource`() =
        runBlocking {
            val oldList = fakeRestaurantRepository.restaurants
            val restaurantToDelete = oldList.last()
            val result = deleteRestaurantUseCase(restaurantId = restaurantToDelete.id).toList()
            assertTrue(result[0] is Resource.Loading)
            assertTrue(result.last() is Resource.Success)
            assertTrue(oldList.size > fakeRestaurantRepository.restaurants.size)
            assertNull(fakeRestaurantRepository.restaurants.find { restaurantToDelete.id == it.id })
        }

    @Test
    fun `When executed with invalid id, should delete restaurant and return success resource`() =
        runBlocking {
            val result = deleteRestaurantUseCase(restaurantId = "asdfadfa").toList()
            assertTrue(result[0] is Resource.Loading)
            assertTrue(result.last() is Resource.Failure)
        }
}