package com.nasportfolio.restaurant.create.branch

import com.google.android.gms.maps.model.LatLng

sealed class CreateBranchEvent {
    class OnAddressChanged(val address: String) : CreateBranchEvent()
    class OnLocationChanged(val latLng: LatLng) : CreateBranchEvent()
    object OnSubmit : CreateBranchEvent()
}