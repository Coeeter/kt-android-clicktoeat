package com.nasportfolio.clicktoeat.domain.utils

import com.google.gson.annotations.SerializedName

sealed class ResourceError {
    data class Default(
        @SerializedName(value = "error", alternate = ["message"])
        val error: String
    ) : ResourceError()

    data class Field(
        val message: String,
        var errors: List<FieldErrorItem>
    ) : ResourceError()

    data class FieldErrorItem(
        val field: String,
        val error: String
    )
}