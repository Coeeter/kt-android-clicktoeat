package com.nasportfolio.domain.validation.usecases

import com.nasportfolio.domain.utils.ResourceError
import javax.inject.Inject

class ValidatePassword @Inject constructor() {
    operator fun invoke(
        value: String,
        flag: String = CREATE_FLAG,
        field: String = "password"
    ): ResourceError.FieldErrorItem? {
        val regex = """^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@${'$'}%^&*-]).{8,}${'$'}"""
        val error: String
        val isValidated: Boolean
        when (flag) {
            CREATE_FLAG -> {
                isValidated = regex.toRegex().matches(value)
                error = "Invalid password provided"
            }
            LOGIN_FLAG -> {
                isValidated = value.isNotEmpty() && value.isNotBlank()
                error = "Password required!"
            }
            else -> throw IllegalArgumentException("Invalid flag provided")
        }
        if (isValidated) return null
        return ResourceError.FieldErrorItem(field, error)
    }

    companion object {
        const val CREATE_FLAG = "CREATE_FLAG"
        const val LOGIN_FLAG = "LOGIN_FLAG"
    }
}