package com.nasportfolio.domain.validation.usecases

import com.nasportfolio.domain.utils.ResourceError
import javax.inject.Inject

class ValidateConfirmPassword @Inject constructor() {
    operator fun invoke(
        value: String,
        password: String,
        field: String = "confirmPassword",
    ): ResourceError.FieldErrorItem? {
        if (password == value) return null
        return ResourceError.FieldErrorItem(
            field,
            error = "Passwords do not match!"
        )
    }
}