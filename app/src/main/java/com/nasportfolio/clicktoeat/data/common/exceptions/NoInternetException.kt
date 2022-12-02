package com.nasportfolio.clicktoeat.data.common.exceptions

import java.io.IOException

class NoInternetException : IOException(
    "No internet available, please check your connected WIFi or Data"
)