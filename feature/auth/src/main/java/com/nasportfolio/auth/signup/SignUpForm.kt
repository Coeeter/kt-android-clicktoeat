package com.nasportfolio.auth.signup

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.with
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
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
import com.nasportfolio.auth.navigation.authScreenRoute
import com.nasportfolio.common.components.CltButton
import com.nasportfolio.common.components.CltHeading
import com.nasportfolio.common.components.CltInput
import com.nasportfolio.common.utils.Screen
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SignUpForm(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState,
    navController: NavHostController,
    changePage: () -> Unit,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by signUpViewModel.signUpState.collectAsState()

    LaunchedEffect(true) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                signUpViewModel.errorChannel.collect {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                    scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
                }
            }
        }
    }

    LaunchedEffect(state.isCreated) {
        if (!state.isCreated) return@LaunchedEffect
        navController.navigate(Screen.HomeScreen.route) {
            popUpTo(authScreenRoute) {
                inclusive = true
            }
        }
    }

    AnimatedContent(
        targetState = state.signUpStage,
        transitionSpec = {
            val direction = when (targetState) {
                SignUpStage.NAME -> AnimatedContentScope.SlideDirection.Right
                SignUpStage.PASSWORD -> AnimatedContentScope.SlideDirection.Left
            }
            slideIntoContainer(
                towards = direction,
                animationSpec = tween(durationMillis = 500)
            ) with slideOutOfContainer(
                towards = direction,
                animationSpec = tween(durationMillis = 500)
            )
        }
    ) { targetState ->
        when (targetState) {
            SignUpStage.NAME -> {
                FirstSignUpFormStage(
                    modifier = modifier,
                    state = state,
                    focusManager = focusManager,
                    signUpViewModel = signUpViewModel,
                    changePage = changePage
                )
            }
            SignUpStage.PASSWORD -> {
                SecondSignUpFormStage(
                    modifier = modifier,
                    state = state,
                    focusManager = focusManager,
                    signUpViewModel = signUpViewModel,
                    changePage = changePage
                )
            }
        }
    }

}

@Composable
private fun FirstSignUpFormStage(
    modifier: Modifier,
    state: SignUpState,
    focusManager: FocusManager,
    signUpViewModel: SignUpViewModel,
    changePage: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(15.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 15.dp
            )
        ) {
            CltHeading(
                text = "Create Account",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
            CltInput(
                modifier = Modifier.fillMaxWidth(),
                value = state.username,
                label = "Username",
                error = state.usernameError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                onValueChange = {
                    signUpViewModel.onEvent(
                        SignUpEvent.OnUsernameChange(username = it)
                    )
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            CltInput(
                modifier = Modifier.fillMaxWidth(),
                value = state.email,
                label = "Email",
                error = state.emailError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                onValueChange = {
                    signUpViewModel.onEvent(
                        SignUpEvent.OnEmailChange(email = it)
                    )
                }
            )
            Spacer(modifier = Modifier.height(30.dp))
            CltButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    focusManager.clearFocus()
                    signUpViewModel.onEvent(
                        event = SignUpEvent.ProceedNextStage
                    )
                },
            ) {
                Text(
                    text = "Next",
                    color = Color.White,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = changePage) {
                    Text(text = "Already have an account? Login here", fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
private fun SecondSignUpFormStage(
    modifier: Modifier,
    state: SignUpState,
    focusManager: FocusManager,
    signUpViewModel: SignUpViewModel,
    changePage: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(15.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 15.dp
            )
        ) {
            CltHeading(
                text = "Secure your Account",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
            CltInput(
                modifier = Modifier.fillMaxWidth(),
                value = state.password,
                label = "Password",
                error = state.passwordError,
                isPassword = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                onValueChange = {
                    signUpViewModel.onEvent(
                        SignUpEvent.OnPasswordChange(password = it)
                    )
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            CltInput(
                modifier = Modifier.fillMaxWidth(),
                value = state.confirmPassword,
                label = "Confirm password",
                error = state.confirmPasswordError,
                isPassword = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                onValueChange = {
                    signUpViewModel.onEvent(
                        SignUpEvent.OnConfirmPasswordChange(confirmPassword = it)
                    )
                }
            )
            Spacer(modifier = Modifier.height(30.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                CltButton(
                    modifier = Modifier.weight(1f),
                    text = "Cancel",
                    withLoading = true,
                    enabled = true,
                    gradient = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFE60000), Color(0xFFFF5E5E))
                    ),
                    onClick = {
                        focusManager.clearFocus()
                        signUpViewModel.onEvent(SignUpEvent.ProceedPrevStage)
                    }
                )
                Spacer(modifier = Modifier.width(10.dp))
                CltButton(
                    modifier = Modifier.weight(1f),
                    text = "Sign Up",
                    withLoading = true,
                    enabled = !state.isLoading,
                    onClick = {
                        focusManager.clearFocus()
                        signUpViewModel.onEvent(SignUpEvent.OnSubmit)
                    }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = changePage) {
                    Text(text = "Already have an account? Login here", fontSize = 15.sp)
                }
            }
        }
    }
}

