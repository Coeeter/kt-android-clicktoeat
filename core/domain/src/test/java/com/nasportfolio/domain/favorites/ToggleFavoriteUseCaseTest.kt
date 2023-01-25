package com.nasportfolio.domain.favorites

import com.nasportfolio.domain.favorites.usecases.ToggleFavoriteUseCase
import com.nasportfolio.domain.restaurant.TransformedRestaurant
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.test.favorites.FakeFavoriteRepository
import com.nasportfolio.test.restaurant.FakeRestaurantRepository
import com.nasportfolio.test.user.FakeUserRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class ToggleFavoriteUseCaseTest {

    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase
    private lateinit var fakeFavoriteRepository: FakeFavoriteRepository
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var closeable: AutoCloseable

    @Mock
    lateinit var transformedRestaurant: TransformedRestaurant

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
        fakeUserRepository = FakeUserRepository()
        fakeFavoriteRepository = FakeFavoriteRepository(
            fakeUserRepository = fakeUserRepository,
            fakeRestaurantRepository = FakeRestaurantRepository()
        )
        toggleFavoriteUseCase = ToggleFavoriteUseCase(
            favoriteRepository = fakeFavoriteRepository,
            userRepository = fakeUserRepository
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
            val oldFavList = fakeFavoriteRepository.favorites
            val result = toggleFavoriteUseCase(transformedRestaurant)
            assertTrue(result is Resource.Success)
            assertNotNull(fakeFavoriteRepository.favorites.find { it.restaurantId == transformedRestaurant.id })
            assertTrue(fakeFavoriteRepository.favorites.size > oldFavList.size)
        }

    @Test
    fun `When removing restaurant from fav, should return success resource and update fav list`() =
        runBlocking {
            whenever(transformedRestaurant.isFavoriteByCurrentUser).thenReturn(true)
            whenever(transformedRestaurant.id).thenReturn("id")
            fakeFavoriteRepository.favorites =
                fakeFavoriteRepository.favorites.toMutableList().apply {
                    add(
                        FakeFavoriteRepository.Favorite(
                            userId = fakeUserRepository.users.last().id,
                            restaurantId = transformedRestaurant.id
                        )
                    )
                }
            val oldFavList = fakeFavoriteRepository.favorites
            val result = toggleFavoriteUseCase(transformedRestaurant)
            assertTrue(result is Resource.Success)
            assertNull(fakeFavoriteRepository.favorites.find { it.restaurantId == transformedRestaurant.id })
            assertTrue(fakeFavoriteRepository.favorites.size < oldFavList.size)
        }
}