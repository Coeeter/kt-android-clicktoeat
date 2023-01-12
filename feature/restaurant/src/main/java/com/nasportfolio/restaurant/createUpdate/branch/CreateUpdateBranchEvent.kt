package com.nasportfolio.restaurant.createUpdate.branch

import com.google.android.gms.maps.model.LatLng

sealed class CreateUpdateBranchEvent {
    class OnAddressChanged(val address: String) : CreateUpdateBranchEvent()
    class OnLocationChanged(val latLng: LatLng) : CreateUpdateBranchEvent()
    object OnSubmit : CreateUpdateBranchEvent()
}