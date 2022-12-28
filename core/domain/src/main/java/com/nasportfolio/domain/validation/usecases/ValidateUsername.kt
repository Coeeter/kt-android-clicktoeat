package com.nasportfolio.domain.validation.usecases

import com.nasportfolio.domain.utils.ResourceError
import javax.inject.Inject

class ValidateUsername @Inject constructor() {
    operator fun invoke(
        value: String,
        field: String = "username"
    ): ResourceError.FieldErrorItem? {
        if (value.isNotEmpty()) return null
        return ResourceError.FieldErrorItem(field, error = "Username required")
    }
}