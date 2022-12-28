package com.nasportfolio.domain.utils

import com.google.gson.annotations.SerializedName

sealed class ResourceError {
    data class DefaultError(
        @SerializedName(value = "error", alternate = ["message"])
        val error: String
    ) : ResourceError()

    data class FieldError(
        val message: String,
        val errors: List<FieldErrorItem>
    ) : ResourceError()

    data class FieldErrorItem(
        val field: String,
        val error: String
    )
}