package com.nasportfolio.clicktoeat.data.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.nasportfolio.clicktoeat.domain.common.exceptions.NoNetworkException
import okhttp3.Interceptor
import okhttp3.Response

class NetworkInterceptor(
    private val context: Context
) : Interceptor {

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val connection = connectivityManager.getNetworkCapabilities(network)
        return connection != null && (connection.hasTransport(
            NetworkCapabilities.TRANSPORT_WIFI
        ) || connection.hasTransport(
            NetworkCapabilities.TRANSPORT_CELLULAR
        ))
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        if (!isConnected()) throw NoNetworkException()
        return chain.proceed(originalRequest)
    }

}