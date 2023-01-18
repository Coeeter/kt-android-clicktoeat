package com.nasportfolio.clicktoeat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasportfolio.common.components.images.bitmapCache
import com.nasportfolio.domain.user.usecases.GetCurrentLoggedInUser
import com.nasportfolio.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentLoggedInUser: GetCurrentLoggedInUser
) : ViewModel() {
    private val _userId = MutableStateFlow<String?>(null)
    val userId = _userId.asStateFlow()

    private val _profileImage = MutableStateFlow<Bitmap?>(null)
    val profileImage = _profileImage.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        updateImage()
    }

    fun updateImage() {
        getCurrentLoggedInUser().onEach { userResource ->
            when (userResource) {
                is Resource.Success -> {
                    _userId.value = userResource.result.id
                    _profileImage.update {
                        val url = userResource.result.image?.url
                        url ?: return@update null
                        return@update bitmapCache[url]?.asAndroidBitmap() ?: run {
                            val bitmap = BitmapFactory.decodeStream(
                                URL(url).openConnection().getInputStream()
                            )
                            bitmapCache[url] = bitmap.asImageBitmap()
                            bitmap
                        }.also {
                            _isLoading.value = false
                        }
                    }
                }
                is Resource.Loading -> _isLoading.value = userResource.isLoading
                else -> _isLoading.value = false
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }
}