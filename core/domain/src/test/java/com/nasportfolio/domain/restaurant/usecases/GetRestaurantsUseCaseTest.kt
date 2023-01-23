package com.nasportfolio.domain.restaurant.usecases

import com.nasportfolio.domain.comment.FakeCommentRepository
import com.nasportfolio.domain.favorites.FavoriteRepository
import com.nasportfolio.domain.likesdislikes.dislike.DislikeRepository
import com.nasportfolio.domain.likesdislikes.like.LikeRepository
import com.nasportfolio.domain.restaurant.FakeRestaurantRepository
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.user.usecases.GetCurrentLoggedInUser
import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.util.*

class GetRestaurantsUseCaseTest {

    private lateinit var getRestaurantsUseCase: GetRestaurantsUseCase
    private lateinit var fakeRestaurantRepository: FakeRestaurantRepository
    private lateinit var closeable: AutoCloseable

    @Mock
    lateinit var getCurrentLoggedInUser: GetCurrentLoggedInUser

    @Mock
    lateinit var favoriteRepository: FavoriteRepository

    @Mock
    lateinit var likeRepository: LikeRepository

    @Mock
    lateinit var dislikeRepository: DislikeRepository

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
        fakeRestaurantRepository = FakeRestaurantRepository()
        getRestaurantsUseCase = GetRestaurantsUseCase(
            restaurantRepository = fakeRestaurantRepository,
            likeRepository = likeRepository,
            dislikeRepository = dislikeRepository,
            favoriteRepository = favoriteRepository,
            commentRepository = FakeCommentRepository(),
            getCurrentLoggedInUserUseCase = getCurrentLoggedInUser
        )
    }

    @After
    fun tearDown() {
        closeable.close()
    }

    @Test
    fun `Should return all restaurants from repository`() = runBlocking {
        val user = User(
            id = "0",
            username = "test",
            email = "test@gmail.com",
            image = null
        )
        whenever(getCurrentLoggedInUser()).thenReturn(flowOf(Resource.Success(user)))
        whenever(
            likeRepository.getUsersWhoLikedComment(anyString())
        ).thenReturn(
            Resource.Success(emptyList())
        )
        whenever(
            dislikeRepository.getUsersWhoDislikedComments(anyString())
        ).thenReturn(
            Resource.Success(emptyList())
        )
        whenever(
            favoriteRepository.getFavoriteRestaurantsOfUser(anyString())
        ).thenReturn(
            Resource.Success(emptyList())
        )
        whenever(
            favoriteRepository.getUsersWhoFavoriteRestaurant(anyString())
        ).thenReturn(
            Resource.Success(emptyList())
        )
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
            whenever(
                likeRepository.getUsersWhoLikedComment(anyString())
            ).thenReturn(
                Resource.Success(emptyList())
            )
            whenever(
                dislikeRepository.getUsersWhoDislikedComments(anyString())
            ).thenReturn(
                Resource.Success(emptyList())
            )
            whenever(
                favoriteRepository.getFavoriteRestaurantsOfUser(anyString())
            ).thenReturn(
                Resource.Success(emptyList())
            )
            whenever(
                favoriteRepository.getUsersWhoFavoriteRestaurant(anyString())
            ).thenReturn(
                Resource.Success(emptyList())
            )
            val user = User(
                id = "0",
                username = "test",
                email = "test@gmail.com",
                image = null
            )
            whenever(getCurrentLoggedInUser()).thenReturn(flowOf(Resource.Success(user)))
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
            whenever(
                likeRepository.getUsersWhoLikedComment(anyString())
            ).thenReturn(
                Resource.Success(emptyList())
            )
            whenever(
                dislikeRepository.getUsersWhoDislikedComments(anyString())
            ).thenReturn(
                Resource.Success(emptyList())
            )
            val user = User(
                id = "0",
                username = "test",
                email = "test@gmail.com",
                image = null
            )
            val restaurantList = fakeRestaurantRepository.restaurants
            val restaurant = restaurantList.last()
            whenever(
                favoriteRepository.getUsersWhoFavoriteRestaurant(anyString())
            ).thenReturn(
                Resource.Success(listOf())
            )
            whenever(
                favoriteRepository.getUsersWhoFavoriteRestaurant(restaurantId = restaurant.id)
            ).thenReturn(
                Resource.Success(listOf(user))
            )
            whenever(
                favoriteRepository.getFavoriteRestaurantsOfUser(anyString())
            ).thenReturn(
                Resource.Success(listOf())
            )
            whenever(
                favoriteRepository.getFavoriteRestaurantsOfUser(userId = user.id)
            ).thenReturn(
                Resource.Success(listOf(restaurant))
            )
            whenever(getCurrentLoggedInUser()).thenReturn(flowOf(Resource.Success(user)))
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