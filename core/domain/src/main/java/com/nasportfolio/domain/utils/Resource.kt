package com.nasportfolio.domain.utils

sealed class Resource<T> {
    object Loading : Resource<Unit>()
    class Success<T>(val result: T) : Resource<T>()
    class Failure<T>(val error: ResourceError) : Resource<T>()
}