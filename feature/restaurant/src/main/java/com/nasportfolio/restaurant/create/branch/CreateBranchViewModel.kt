package com.nasportfolio.restaurant.create.branch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nasportfolio.domain.branch.usecases.CreateBranchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CreateBranchViewModel @Inject constructor(
    private val createBranchUseCase: CreateBranchUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow("")
    val state = _state.asStateFlow()

    init {
        savedStateHandle.get<String>("restaurantId")?.let {
            _state.value = it
        }
    }
}