package com.nasportfolio.common.utils

fun Double.toStringAsFixed(digits: Int = 0): String {
    return "%.${digits}f".format(this)
}