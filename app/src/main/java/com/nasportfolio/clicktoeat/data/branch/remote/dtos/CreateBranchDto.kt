package com.nasportfolio.clicktoeat.data.branch.remote.dtos

data class CreateBranchDto(
    val address: String,
    val latitude: Double,
    val longitude: Double
)