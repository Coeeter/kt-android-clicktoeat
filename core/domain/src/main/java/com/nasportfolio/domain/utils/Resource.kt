package com.nasportfolio.domain.utils

sealed class Resource<T> {
    class Loading<T>(val isLoading: Boolean) : Resource<T>()
    class Success<T>(val result: T) : Resource<T>()
    class Failure<T>(val error: ResourceError) : Resource<T>()
}