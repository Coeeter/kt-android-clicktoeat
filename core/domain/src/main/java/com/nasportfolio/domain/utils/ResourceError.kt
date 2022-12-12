package com.nasportfolio.domain.utils

import com.google.gson.annotations.SerializedName

sealed class ResourceError {
    class DefaultError(
        @SerializedName(value = "error", alternate = ["message"])
        val error: String
    ) : ResourceError()

    class FieldError(
        val message: String,
        val errors: List<FieldErrorItem>
    ) : ResourceError()

    class FieldErrorItem(
        val field: String,
        val error: String
    )
}