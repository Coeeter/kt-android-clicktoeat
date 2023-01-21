package com.nasportfolio.user.update.account

import android.graphics.Bitmap

sealed class UpdateUserEvent {
    class OnUsernameChange(val username: String) : UpdateUserEvent()
    class OnEmailChange(val email: String) : UpdateUserEvent()
    class OnImageChange(val image: Bitmap) : UpdateUserEvent()
    object OnRemoveImage : UpdateUserEvent()
    object OnSubmit : UpdateUserEvent()
}