package com.nasportfolio.restaurant.createUpdate.branch

import com.google.android.gms.maps.model.LatLng

data class CreateUpdateBranchState(
    val restaurantId: String? = null,
    val address: String = "",
    val latLng: LatLng? = null,
    val addressError: String? = null,
    val latLngError: String? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isCreated: Boolean = false,
    val branchId: String? = null,
    val isUpdateForm: Boolean = false,
    val isUpdated: Boolean = false,
)