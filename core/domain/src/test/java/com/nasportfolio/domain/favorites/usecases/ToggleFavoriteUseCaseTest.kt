package com.nasportfolio.domain.favorites.usecases

import com.nasportfolio.domain.favorites.FakeFavoriteRepository
import com.nasportfolio.domain.restaurant.TransformedRestaurant
import com.nasportfolio.domain.user.FakeUserRepository
import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class ToggleFavoriteUseCaseTest {

    lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase
    lateinit var fakeFavoriteRepository: FakeFavoriteRepository
    lateinit var closeable: AutoCloseable

    @Mock
    lateinit var transformedRestaurant: TransformedRestaurant

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
        fakeFavoriteRepository = FakeFavoriteRepository()
        toggleFavoriteUseCase = ToggleFavoriteUseCase(
            favoriteRepository = fakeFavoriteRepository,
            userRepository = FakeUserRepository()
        )
    }

    @After
    fun tearDown() {
        closeable.close()
    }

    @Test
    fun `When adding restaurant to fav, should return success resource and update fav list`() =
        runBlocking {
            whenever(transformedRestaurant.isFavoriteByCurrentUser).thenReturn(false)
            whenever(transformedRestaurant.id).thenReturn("id")
            val oldFavList = fakeFavoriteRepository.favoriteRestaurants
            val result = toggleFavoriteUseCase(transformedRestaurant)
            assertTrue(result is Resource.Success)
            assertNotNull(fakeFavoriteRepository.favoriteRestaurants.find { it == transformedRestaurant.id })
            assertTrue(fakeFavoriteRepository.favoriteRestaurants.size > oldFavList.size)
        }

    @Test
    fun `When removing restaurant from fav, should return success resource and update fav list`() =
        runBlocking {
            whenever(transformedRestaurant.isFavoriteByCurrentUser).thenReturn(true)
            whenever(transformedRestaurant.id).thenReturn("id")
            fakeFavoriteRepository.favoriteRestaurants =
                fakeFavoriteRepository.favoriteRestaurants.toMutableList().apply {
                    add("id")
                }
            val oldFavList = fakeFavoriteRepository.favoriteRestaurants
            val result = toggleFavoriteUseCase(transformedRestaurant)
            assertTrue(result is Resource.Success)
            assertNull(fakeFavoriteRepository.favoriteRestaurants.find { it == transformedRestaurant.id })
            assertTrue(fakeFavoriteRepository.favoriteRestaurants.size < oldFavList.size)
        }
}