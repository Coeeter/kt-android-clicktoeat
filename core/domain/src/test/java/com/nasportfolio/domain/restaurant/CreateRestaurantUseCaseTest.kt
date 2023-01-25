package com.nasportfolio.domain.restaurant

import android.graphics.Bitmap
import com.nasportfolio.domain.restaurant.usecases.CreateRestaurantUseCase
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import com.nasportfolio.test.restaurant.FakeRestaurantRepository
import com.nasportfolio.test.user.FakeUserRepository
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class CreateRestaurantUseCaseTest {

    private lateinit var fakeRestaurantRepository: FakeRestaurantRepository
    private lateinit var createRestaurantUseCase: CreateRestaurantUseCase
    private lateinit var closeable: AutoCloseable

    @Mock
    lateinit var bitmap: Bitmap

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
        fakeRestaurantRepository = FakeRestaurantRepository()
        createRestaurantUseCase = CreateRestaurantUseCase(
            userRepository = FakeUserRepository(),
            restaurantRepository = fakeRestaurantRepository
        )
    }

    @After
    fun tearDown() {
        closeable.close()
    }

    @Test
    fun `When given invalid inputs, should return failure resource`() = runBlocking {
        val result = createRestaurantUseCase(
            name = "",
            description = "",
            image = null
        ).last()
        assertEquals(true, result is Resource.Failure)
        val failure = result as Resource.Failure
        assertEquals(true, failure.error is ResourceError.FieldError)
        val fieldError = failure.error as ResourceError.FieldError
        assertEquals(3, fieldError.errors.size)
    }

    @Test
    fun `When given valid inputs, should return success resource`() = runBlocking {
        val oldList = fakeRestaurantRepository.restaurants
        val result = createRestaurantUseCase(
            name = "test",
            description = "test",
            image = bitmap
        ).toList()
        assertEquals(true, result[0] is Resource.Loading)
        assertEquals(true, result.last() is Resource.Success)
        val insertId = (result.last() as Resource.Success).result
        assertNotEquals(oldList, fakeRestaurantRepository.restaurants)
        assertNotNull(fakeRestaurantRepository.restaurants.find { it.id == insertId })
    }
}