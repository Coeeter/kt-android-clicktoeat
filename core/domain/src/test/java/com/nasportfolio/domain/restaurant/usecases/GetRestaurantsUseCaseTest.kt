package com.nasportfolio.domain.restaurant.usecases

import com.nasportfolio.domain.comment.FakeCommentRepository
import com.nasportfolio.domain.favorites.FakeFavoriteRepository
import com.nasportfolio.domain.likesdislikes.FakeDislikeRepository
import com.nasportfolio.domain.likesdislikes.FakeLikeRepository
import com.nasportfolio.domain.restaurant.FakeRestaurantRepository
import com.nasportfolio.domain.user.FakeUserRepository
import com.nasportfolio.domain.user.usecases.GetCurrentLoggedInUser
import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.*

class GetRestaurantsUseCaseTest {

    private lateinit var getRestaurantsUseCase: GetRestaurantsUseCase
    private lateinit var fakeRestaurantRepository: FakeRestaurantRepository
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var favoriteRepository: FakeFavoriteRepository

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        val fakeCommentRepository = FakeCommentRepository()
        fakeRestaurantRepository = FakeRestaurantRepository()
        favoriteRepository = FakeFavoriteRepository(
            fakeUserRepository = fakeUserRepository,
            fakeRestaurantRepository = fakeRestaurantRepository
        )
        getRestaurantsUseCase = GetRestaurantsUseCase(
            restaurantRepository = fakeRestaurantRepository,
            likeRepository = FakeLikeRepository(
                fakeUserRepository = fakeUserRepository,
                fakeCommentRepository = fakeCommentRepository
            ),
            dislikeRepository = FakeDislikeRepository(
                fakeUserRepository = fakeUserRepository,
                fakeCommentRepository = fakeCommentRepository
            ),
            favoriteRepository = favoriteRepository,
            commentRepository = fakeCommentRepository,
            getCurrentLoggedInUserUseCase = GetCurrentLoggedInUser(
                userRepository = fakeUserRepository
            )
        )
    }

    @Test
    fun `Should return all restaurants from repository`() = runBlocking {
        val user = fakeUserRepository.users.last()
        val restaurantList = fakeRestaurantRepository.restaurants
        val result = getRestaurantsUseCase().toList()
        assertTrue(result[0] is Resource.Loading)
        assertTrue(result.last() is Resource.Success)
        val transformedRestaurantList = (result.last() as Resource.Success).result
        assertTrue(transformedRestaurantList.isNotEmpty())
        assertEquals(restaurantList.size, transformedRestaurantList.size)
        transformedRestaurantList.forEachIndexed { index, transformedRestaurant ->
            assertEquals(restaurantList[index].id, transformedRestaurant.id)
        }
    }

    @Test
    fun `When id is specified, should return restaurant with that id from repository`() =
        runBlocking {
            val restaurantList = fakeRestaurantRepository.restaurants
            val index = Random().nextInt(restaurantList.size)
            val result = getRestaurantsUseCase.getById(
                restaurantId = restaurantList[index].id
            ).toList()
            assertTrue(result[0] is Resource.Loading)
            assertTrue(result.last() is Resource.Success)
            val transformedRestaurant = (result.last() as Resource.Success).result
            assertEquals(restaurantList[index].id, transformedRestaurant.id)
        }

    @Test
    fun `When filter specified, should return filtered restaurants from repository`() =
        runBlocking {
            val user = fakeUserRepository.users.last()
            val restaurantList = fakeRestaurantRepository.restaurants
            val index = Random().nextInt(restaurantList.size)
            val restaurant = restaurantList[index]
            favoriteRepository.favorites = listOf(
                FakeFavoriteRepository.Favorite(
                    restaurantId = restaurant.id,
                    userId = user.id
                )
            )
            val result = getRestaurantsUseCase(
                filter = GetRestaurantsUseCase.Filter.UserIdInFav(user.id)
            ).toList()
            assertTrue(result[0] is Resource.Loading)
            assertTrue(result.last() is Resource.Success)
            val transformedRestaurantList = (result.last() as Resource.Success).result
            assertEquals(1, transformedRestaurantList.size)
            assertEquals(restaurant.id, transformedRestaurantList[0].id)
            assertTrue(transformedRestaurantList[0].isFavoriteByCurrentUser)
        }
}