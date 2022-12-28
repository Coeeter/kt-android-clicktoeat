package com.nasportfolio.auth.signup

sealed class SignUpEvent {
    class OnUsernameChange(val username: String) : SignUpEvent()
    class OnEmailChange(val email: String) : SignUpEvent()
    class OnPasswordChange(val password: String) : SignUpEvent()
    class OnConfirmPasswordChange(val confirmPassword: String) : SignUpEvent()
    object ProceedNextStage : SignUpEvent()
    object ProceedPrevStage : SignUpEvent()
    object OnSubmit : SignUpEvent()
}
