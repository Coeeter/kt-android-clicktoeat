package com.nasportfolio.network.exceptions

import java.io.IOException

class NoInternetException : IOException(
    "No internet available, please check your connected WIFi or Data"
)