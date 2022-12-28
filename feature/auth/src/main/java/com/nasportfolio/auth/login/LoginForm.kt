package com.nasportfolio.auth.login

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.nasportfolio.common.components.CltButton
import com.nasportfolio.common.components.CltHeading
import com.nasportfolio.common.components.CltInput
import com.nasportfolio.common.utils.Screen
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun LoginForm(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState,
    navController: NavHostController,
    changePage: () -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by loginViewModel.loginState.collectAsState()

    LaunchedEffect(true) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.errorChannel.collect {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                    scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
                }
            }
        }
    }

    LaunchedEffect(state.isLoggedIn) {
        if (!state.isLoggedIn) return@LaunchedEffect
        navController.navigate(Screen.HomeScreen.route)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(15.dp),
        elevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 15.dp
            )
        ) {
            CltHeading(
                text = "Login",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
            LoginInputs(
                state = state,
                focusManager = focusManager,
                loginViewModel = loginViewModel
            )
            Spacer(modifier = Modifier.height(5.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(
                    contentPadding = PaddingValues(
                        vertical = 8.dp,
                        horizontal = 5.dp
                    ),
                    onClick = { /*TODO*/ },
                ) {
                    Text(text = "Forgot password?")
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            CltButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Login",
                withLoading = true,
                enabled = !state.isLoading,
                onClick = {
                    focusManager.clearFocus()
                    loginViewModel.onEvent(LoginEvent.OnSubmit)
                },
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = changePage) {
                    Text(text = "Don't have an account? Sign up here", fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
private fun LoginInputs(
    state: LoginState,
    focusManager: FocusManager,
    loginViewModel: LoginViewModel
) {
    CltInput(
        modifier = Modifier.fillMaxWidth(),
        value = state.email,
        label = "Email",
        error = state.emailError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        ),
        onValueChange = {
            loginViewModel.onEvent(
                LoginEvent.OnEmailChange(email = it)
            )
        }
    )
    Spacer(modifier = Modifier.height(10.dp))
    CltInput(
        modifier = Modifier.fillMaxWidth(),
        value = state.password,
        label = "Password",
        error = state.passwordError,
        isPassword = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        ),
        onValueChange = {
            loginViewModel.onEvent(
                LoginEvent.OnPasswordChange(password = it)
            )
        }
    )
}