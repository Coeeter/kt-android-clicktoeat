package com.nasportfolio.clicktoeat

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nasportfolio.domain.user.usecases.UpdateAccountUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class NotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var updateAccountUseCase: UpdateAccountUseCase

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        updateAccountUseCase.updateFcmToken(fcmToken = token)?.let {
            _snackBarChannel.trySend(it)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        println("debug: $message")
        val notification = message.notification ?: return
        val title = notification.title ?: return
        val body = notification.body?.lowercase() ?: return
        message.notification?.title?.let {
            _snackBarChannel.trySend("$title $body")
        }
    }

    companion object {
        private val _snackBarChannel = Channel<String>()
        val snackBarChannel = _snackBarChannel.receiveAsFlow()
    }
}