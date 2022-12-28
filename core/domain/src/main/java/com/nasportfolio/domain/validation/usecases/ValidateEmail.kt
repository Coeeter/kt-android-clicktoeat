package com.nasportfolio.domain.validation.usecases

import com.nasportfolio.domain.utils.ResourceError
import javax.inject.Inject

class ValidateEmail @Inject constructor() {
    operator fun invoke(value: String, field: String = "email"): ResourceError.FieldErrorItem? {
        var error = "Invalid email provided"
        val regex = """^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}${'$'}""".toRegex(
            RegexOption.IGNORE_CASE
        )
        if (error.isEmpty()) error = "Email required!"
        if (regex.matches(value)) return null
        return ResourceError.FieldErrorItem(field, error)
    }
}