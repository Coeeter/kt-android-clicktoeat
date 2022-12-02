package com.nasportfolio.clicktoeat.domain.utils

sealed class ResourceError {
    data class Default(val error: String) : ResourceError()

    data class Field(
        val message: String,
        var errors: List<FieldErrorItem>
    ) : ResourceError()

    data class FieldErrorItem(
        val field: String,
        val error: String
    )
}