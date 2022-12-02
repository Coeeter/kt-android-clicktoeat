package com.nasportfolio.clicktoeat.data.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.nasportfolio.clicktoeat.data.common.exceptions.NoInternetException
import com.nasportfolio.clicktoeat.data.common.exceptions.NoNetworkException
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

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

    private fun isInternetAvailable(): Boolean {
        return try {
            val timeOutMs = 1500
            val sock = Socket()
            val socketAddress = InetSocketAddress("8.8.8.8", 53)
            sock.connect(socketAddress)
            sock.close()
            true
        } catch (e: IOException) {
            false
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        if (!isConnected()) throw NoNetworkException()
        if (!isInternetAvailable()) throw NoInternetException()
        return chain.proceed(originalRequest)
    }

}