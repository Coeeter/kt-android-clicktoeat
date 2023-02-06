package com.nasportfolio.restaurant.home

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.nasportfolio.domain.branch.usecases.GetBranchUseCase
import com.nasportfolio.domain.favorites.usecases.ToggleFavoriteUseCase
import com.nasportfolio.domain.restaurant.usecases.GetRestaurantsUseCase
import com.nasportfolio.domain.user.usecases.GetCurrentLoggedInUser
import com.nasportfolio.domain.user.usecases.LogOutUseCase
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val logOutUseCase: LogOutUseCase,
    private val getRestaurantsUseCase: GetRestaurantsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getCurrentLoggedInUser: GetCurrentLoggedInUser,
    private val getBranchUseCase: GetBranchUseCase,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorChannel = _errorChannel.receiveAsFlow()

    init {
        getLoggedInUser()
        getBranches()
        getRestaurants()
        getCurrentLocation()
    }

    fun refreshPage() {
        _state.update { state ->
            state.copy(isRefreshing = true)
        }
        getRestaurants(fetchFromRemote = true)
        getBranches(fetchFromRemote = true)
        getCurrentLocation()
    }

    fun toggleFavorite(restaurantId: String) {
        viewModelScope.launch {
            val index = _state.value.restaurantList.map { it.id }.indexOf(restaurantId)
            val restaurant = _state.value.restaurantList[index]
            val isFavorited = restaurant.favoriteUsers
                .map { it.id }
                .contains(_state.value.currentUser?.id)
            lateinit var oldState: HomeState
            _state.update { state ->
                oldState = state
                state.copy(
                    restaurantList = state.restaurantList.toMutableList().apply {
                        set(
                            index,
                            restaurant.copy(
                                favoriteUsers = restaurant.favoriteUsers.filter {
                                    if (!isFavorited) return@filter true
                                    it.id != state.currentUser?.id
                                }.toMutableList().apply list@{
                                    if (isFavorited) return@list
                                    add(state.currentUser!!)
                                }
                            )
                        )
                    }
                )
            }
            when (val resource = toggleFavoriteUseCase(restaurant)) {
                is Resource.Failure -> {
                    _state.value = oldState
                    if (resource.error !is ResourceError.DefaultError) return@launch
                    val defaultError = resource.error as ResourceError.DefaultError
                    _errorChannel.send(defaultError.error)
                }
                else -> Unit
            }
        }
    }

    private fun getLoggedInUser() {
        getCurrentLoggedInUser().onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(currentUser = it.result)
                }
                is Resource.Failure -> {
                    if (it.error !is ResourceError.DefaultError) return@onEach
                    val defaultError = it.error as ResourceError.DefaultError
                    _errorChannel.send(defaultError.error)
                }
                else -> Unit
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun getRestaurants(fetchFromRemote: Boolean = false) {
        getRestaurantsUseCase(fetchFromRemote = fetchFromRemote).onEach { restaurantResource ->
            when (restaurantResource) {
                is Resource.Success -> _state.update { state ->
                    val restaurantList = restaurantResource.result.sortedBy { it.name }
                    val favRestaurants = restaurantList
                        .filter { it.favoriteUsers.map { it.id }.contains(state.currentUser!!.id) }
                        .map { restaurantList.indexOf(it) }
                    val featuredRestaurants = restaurantList
                        .sortedByDescending { it.averageRating }
                        .slice(0 until if (restaurantList.size < 5) restaurantList.size else 5)
                        .map { restaurantList.indexOf(it) }

                    state.copy(
                        restaurantList = restaurantList,
                        isLoading = false,
                        isRefreshing = false,
                        favRestaurants = favRestaurants,
                        featuredRestaurants = featuredRestaurants
                    )
                }
                is Resource.Failure -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                    if (restaurantResource.error !is ResourceError.DefaultError) return@onEach
                    val defaultError = restaurantResource.error as ResourceError.DefaultError
                    println("debug: ${defaultError.error}")
                    _errorChannel.send(defaultError.error)
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isLoading = restaurantResource.isLoading)
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun getBranches(fetchFromRemote: Boolean = false) {
        getBranchUseCase(fetchFromRemote = fetchFromRemote).onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(branches = it.result)
                }
                is Resource.Failure -> {
                    if (it.error !is ResourceError.DefaultError) return@onEach
                    val defaultError = it.error as ResourceError.DefaultError
                    println("debug: ${defaultError.error}")
                    _errorChannel.send(defaultError.error)
                }
                else -> Unit
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val task = fusedLocationProviderClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun isCancellationRequested() = false
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token
            }
        )
        task.addOnCompleteListener {
            it.exception?.let {
                return@addOnCompleteListener runBlocking {
                    println("debug: ${it.message}")
                    _errorChannel.send(it.message.toString())
                }
            }
            it.result ?: return@addOnCompleteListener getLastLocation()
            _state.update { state ->
                state.copy(
                    currentLocation = LatLng(
                        it.result.latitude,
                        it.result.longitude
                    )
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener {
            it.exception?.let {
                return@addOnCompleteListener runBlocking {
                    _errorChannel.send(it.message.toString())
                }
            }
            it.result ?: return@addOnCompleteListener
            _state.update { state ->
                state.copy(
                    currentLocation = LatLng(
                        it.result.latitude,
                        it.result.longitude
                    )
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            logOutUseCase()
        }
    }
}