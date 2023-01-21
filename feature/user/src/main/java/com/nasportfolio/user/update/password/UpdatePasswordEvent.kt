package com.nasportfolio.user.update.password

sealed class UpdatePasswordEvent {
    class OnOldPasswordChange(val oldPassword: String) : UpdatePasswordEvent()
    class OnNewPasswordChange(val newPassword: String) : UpdatePasswordEvent()
    class OnConfirmNewPasswordChange(val confirmNewPassword: String) : UpdatePasswordEvent()
    object OnSubmit : UpdatePasswordEvent()
}