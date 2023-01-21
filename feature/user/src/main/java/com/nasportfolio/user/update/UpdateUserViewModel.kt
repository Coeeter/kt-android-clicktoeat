package com.nasportfolio.user.update

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasportfolio.common.components.images.bitmapCache
import com.nasportfolio.domain.user.usecases.GetCurrentLoggedInUser
import com.nasportfolio.domain.user.usecases.UpdateAccountUseCase
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.io.ByteArrayOutputStream
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class UpdateUserViewModel @Inject constructor(
    private val getCurrentLoggedInUser: GetCurrentLoggedInUser,
    private val updateAccountUseCase: UpdateAccountUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(UpdateUserState())
    val state = _state.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorChannel = _errorChannel.receiveAsFlow()

    private val _updatedChannel = Channel<Unit>()
    val updatedChannel = _updatedChannel.receiveAsFlow()

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        getCurrentLoggedInUser().onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        isLoading = false,
                        userId = it.result.id,
                        username = it.result.username,
                        email = it.result.email,
                        image = getImageFromUrl(url = it.result.image?.url)
                    )
                }
                is Resource.Failure -> {
                    _state.value = _state.value.copy(isLoading = false)
                    if (it.error !is ResourceError.DefaultError) return@onEach
                    val defaultError = (it.error as ResourceError.DefaultError).error
                    _errorChannel.send(defaultError)
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isLoading = it.isLoading)
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun editPhoto(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val byteArray = baos.toByteArray()
        updateAccountUseCase.updateImage(byteArray).onEach {
            when (it) {
                is Resource.Success -> {
                    _state.update { state ->
                        bitmapCache[it.result.image!!.url] = bitmap.asImageBitmap()
                        state.copy(
                            image = bitmap,
                            isUpdated = true,
                            isImageSubmitting = false
                        )
                    }
                    _updatedChannel.send(Unit)
                }
                is Resource.Failure -> {
                    _state.value = _state.value.copy(isImageSubmitting = false)
                    if (it.error !is ResourceError.DefaultError) return@onEach
                    val defaultError = (it.error as ResourceError.DefaultError).error
                    _errorChannel.send(defaultError)
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isImageSubmitting = it.isLoading)
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun deletePhoto() {
        updateAccountUseCase.deleteImage().onEach {
            when (it) {
                is Resource.Success -> {
                    _state.update { state ->
                        state.copy(
                            image = null,
                            isImageSubmitting = false,
                            isUpdated = true
                        )
                    }
                    _updatedChannel.send(Unit)
                }
                is Resource.Failure -> {
                    _state.value = _state.value.copy(isImageSubmitting = false)
                    if (it.error !is ResourceError.DefaultError) return@onEach
                    val defaultError = (it.error as ResourceError.DefaultError).error
                    _errorChannel.send(defaultError)
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isImageSubmitting = it.isLoading)
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun submit() {
        updateAccountUseCase(
            username = _state.value.username,
            email = _state.value.email
        ).onEach {
            when (it) {
                is Resource.Success -> {
                    _state.update { state ->
                        state.copy(
                            isSubmitting = false,
                            isUpdated = true,
                            username = it.result.username,
                            email = it.result.email,
                            image = getImageFromUrl(url = it.result.image?.url)
                        )
                    }
                    _updatedChannel.send(Unit)
                }
                is Resource.Failure -> {
                    _state.value = _state.value.copy(isSubmitting = false)
                    when (it.error) {
                        is ResourceError.DefaultError -> _errorChannel.send(
                            (it.error as ResourceError.DefaultError).error
                        )
                        is ResourceError.FieldError -> _state.update { state ->
                            val fieldErrors = (it.error as ResourceError.FieldError).errors
                            state.copy(
                                usernameError = fieldErrors.find { it.field == "username" }?.error,
                                emailError = fieldErrors.find { it.field == "email" }?.error,
                            )
                        }
                    }
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isSubmitting = it.isLoading)
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun getImageFromUrl(url: String?) = url?.let {
        bitmapCache[it]?.asAndroidBitmap() ?: run {
            val bitmap = BitmapFactory.decodeStream(
                URL(it).openConnection().getInputStream()
            )
            bitmapCache[it] = bitmap.asImageBitmap()
            bitmap
        }
    }

    fun onEvent(event: UpdateUserEvent) {
        when (event) {
            is UpdateUserEvent.OnUsernameChange -> _state.update { state ->
                state.copy(
                    username = event.username,
                    usernameError = null
                )
            }
            is UpdateUserEvent.OnEmailChange -> _state.update { state ->
                state.copy(
                    email = event.email,
                    emailError = null
                )
            }
            is UpdateUserEvent.OnImageChange -> editPhoto(bitmap = event.image)
            is UpdateUserEvent.OnRemoveImage -> deletePhoto()
            is UpdateUserEvent.OnSubmit -> submit()
        }
    }
}