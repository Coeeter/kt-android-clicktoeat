package com.nasportfolio.clicktoeat.screens.auth.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.nasportfolio.clicktoeat.components.CltButton
import com.nasportfolio.clicktoeat.components.CltInput
import com.nasportfolio.clicktoeat.theme.lightOrange
import com.nasportfolio.clicktoeat.theme.mediumOrange
import com.nasportfolio.clicktoeat.utils.Screen
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginForm(
    scaffoldState: ScaffoldState,
    navController: NavHostController,
    changePage: () -> Unit,
    modifier: Modifier = Modifier,
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

    Box(contentAlignment = Alignment.Center) {
        Surface(
            modifier = modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(15.dp),
            elevation = 4.dp,
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 15.dp
                )
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Login",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h5.copy(
                        color = mediumOrange,
                        fontSize = 25.sp
                    )
                )
                Spacer(modifier = Modifier.height(25.dp))
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
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
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
                        onDone = { focusManager.clearFocus() }
                    ),
                    onValueChange = {
                        loginViewModel.onEvent(
                            LoginEvent.OnPasswordChange(password = it)
                        )
                    }
                )
                Spacer(modifier = Modifier.height(40.dp))
                CltButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                    onClick = {
                        focusManager.clearFocus()
                        loginViewModel.onEvent(LoginEvent.OnSubmit)
                    }
                ) {
                    AnimatedContent(targetState = state.isLoading) { isLoading ->
                        if (isLoading)
                            return@AnimatedContent CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 3.dp
                            )
                        Text(text = "Login", color = Color.White)
                    }
                }
            }
        }
    }
}