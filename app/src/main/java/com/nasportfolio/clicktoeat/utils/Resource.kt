package com.nasportfolio.clicktoeat.utils

sealed class Resource<T> {
    class Success<T>(val result: T) : Resource<T>()
    class Failure<T>(val error: String): Resource<T>()
}