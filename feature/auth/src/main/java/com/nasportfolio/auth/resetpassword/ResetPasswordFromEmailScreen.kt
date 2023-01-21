package com.nasportfolio.auth.resetpassword

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nasportfolio.common.components.buttons.CltButton
import com.nasportfolio.common.components.effects.CltLaunchFlowCollector
import com.nasportfolio.common.components.form.CltInput
import com.nasportfolio.common.components.typography.CltHeading
import com.nasportfolio.common.modifier.gradientBackground
import com.nasportfolio.common.navigation.navigateToAuthScreen
import com.nasportfolio.common.navigation.resetPasswordFromEmailRoute
import com.nasportfolio.common.theme.lightOrange
import com.nasportfolio.common.theme.mediumOrange

@Composable
internal fun ResetPasswordFromEmailScreen(
    navController: NavHostController,
    resetPasswordViewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val focusManager = LocalFocusManager.current
    val scaffoldState = rememberScaffoldState()
    val state by resetPasswordViewModel.state.collectAsState()
    var isInvalidLink by remember {
        mutableStateOf(false)
    }

    CltLaunchFlowCollector(
        lifecycleOwner = lifecycleOwner,
        flow = resetPasswordViewModel.errorChannel
    ) {
        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
        scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
    }

    LaunchedEffect(state.token, state.isLoading) {
        if (state.isLoading) return@LaunchedEffect
        state.token?.let { return@LaunchedEffect }
        isInvalidLink = true
    }

    LaunchedEffect(state.isUpdated) {
        if (!state.isUpdated) return@LaunchedEffect
        navController.navigateToAuthScreen(
            popUpTo = resetPasswordFromEmailRoute
        )
    }

    BackHandler(enabled = true) {
        navController.navigateToAuthScreen(
            popUpTo = resetPasswordFromEmailRoute
        )
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = "Reset password") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateToAuthScreen(
                                popUpTo = resetPasswordFromEmailRoute
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    if (isInvalidLink && !state.isLoading) {
                        PaddingValues(
                            horizontal = 16.dp,
                            vertical = 10.dp
                        )
                    } else {
                        PaddingValues()
                    }
                )
        ) {
            if (isInvalidLink && !state.isLoading) Surface(
                elevation = 4.dp,
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CltHeading(text = "Invalid link")
                    Text(text = "Link might have been expired or password has already been changed")
                }
            }
            if (state.isLoading) Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            if (!state.isLoading && !isInvalidLink) Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                        .gradientBackground(
                            brush = Brush.linearGradient(
                                colors = listOf(lightOrange, mediumOrange)
                            )
                        ),
                )
                Spacer(modifier = Modifier.height(20.dp))
                Surface(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        CltInput(
                            value = state.password,
                            label = "Password",
                            error = state.passwordError,
                            isPassword = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            onValueChange = {
                                resetPasswordViewModel.onEvent(
                                    event = ResetPasswordEvent.OnPasswordChanged(it)
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        CltInput(
                            value = state.confirmPassword,
                            label = "Confirm password",
                            error = state.confirmPasswordError,
                            isPassword = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            onValueChange = {
                                resetPasswordViewModel.onEvent(
                                    event = ResetPasswordEvent.OnConfirmPasswordChanged(it)
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        CltButton(
                            text = "Reset Password",
                            withLoading = true,
                            enabled = !state.isSubmitting,
                            onClick = {
                                focusManager.clearFocus()
                                resetPasswordViewModel.onEvent(
                                    event = ResetPasswordEvent.OnSubmit
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}