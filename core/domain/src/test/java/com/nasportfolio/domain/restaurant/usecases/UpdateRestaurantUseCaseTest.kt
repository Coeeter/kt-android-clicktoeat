package com.nasportfolio.domain.restaurant.usecases

import android.graphics.Bitmap
import com.nasportfolio.domain.restaurant.FakeRestaurantRepository
import com.nasportfolio.domain.user.FakeUserRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class UpdateRestaurantUseCaseTest {

    lateinit var fakeRestaurantRepository: FakeRestaurantRepository
    lateinit var updateRestaurantUseCase: UpdateRestaurantUseCase
    lateinit var closeable: AutoCloseable

    @Mock
    lateinit var bitmap: Bitmap

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
        fakeRestaurantRepository = FakeRestaurantRepository()
        updateRestaurantUseCase = UpdateRestaurantUseCase(
            restaurantRepository = fakeRestaurantRepository,
            userRepository = FakeUserRepository()
        )
    }

    @After
    fun tearDown() {
        closeable.close()
    }

    @Test
    fun `When given invalid inputs should return failure resource`() = runBlocking {
        val result = updateRestaurantUseCase(
            restaurantId = "",
            name = "",
            description = "",
            image = null
        ).last()
        assertTrue(result is Resource.Failure)
        val failure = result as Resource.Failure
        assertTrue(failure.error is ResourceError.FieldError)
        val fieldError = failure.error as ResourceError.FieldError
        assertEquals(3, fieldError.errors.size)
    }

    @Test
    fun `When given valid inputs should return success resource and update restaurant`() = runBlocking {
        val oldRestaurant = fakeRestaurantRepository.restaurants.last()
        val result = updateRestaurantUseCase(
            restaurantId = oldRestaurant.id,
            name = "updatedName",
            description = "updatedDescription",
            image = bitmap
        ).toList()
        assertTrue(result[0] is Resource.Loading)
        assertTrue(result.last() is Resource.Success)
        val restaurant = (result.last() as Resource.Success).result
        assertEquals(oldRestaurant.id, restaurant.id)
        assertEquals("updatedName", restaurant.name)
        assertEquals("updatedDescription", restaurant.description)
        assertEquals(oldRestaurant.image.id + 1, restaurant.image.id)
    }
}