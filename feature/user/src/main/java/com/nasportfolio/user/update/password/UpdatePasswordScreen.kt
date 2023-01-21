package com.nasportfolio.user.update.password

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.nasportfolio.common.modifier.gradientBackground
import com.nasportfolio.common.theme.lightOrange
import com.nasportfolio.common.theme.mediumOrange

@Composable
fun UpdatePasswordScreen(
    navController: NavHostController,
    updatePasswordViewModel: UpdatePasswordViewModel = hiltViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val focusManager = LocalFocusManager.current
    val scaffoldState = rememberScaffoldState()
    val state by updatePasswordViewModel.state.collectAsState()

    val brush = Brush.linearGradient(
        colors = listOf(
            lightOrange,
            mediumOrange
        )
    )

    CltLaunchFlowCollector(
        lifecycleOwner = lifecycleOwner,
        flow = updatePasswordViewModel.errorChannel,
    ) {
        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
        scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
    }

    LaunchedEffect(state.isUpdated) {
        if (!state.isUpdated) return@LaunchedEffect
        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
        scaffoldState.snackbarHostState.showSnackbar(
            message = "Successfully updated password",
            actionLabel = "Okay"
        )
        navController.popBackStack()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = "Update password") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .border(width = 5.dp, brush = brush, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(30.dp)
                        .gradientBackground(brush = brush),
                )
            }
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
                        value = state.oldPassword,
                        label = "Old password",
                        error = state.oldPasswordError,
                        isPassword = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        onValueChange = {
                            updatePasswordViewModel.onEvent(
                                event = UpdatePasswordEvent.OnOldPasswordChange(it)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CltInput(
                        value = state.newPassword,
                        label = "New password",
                        error = state.newPasswordError,
                        isPassword = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        onValueChange = {
                            updatePasswordViewModel.onEvent(
                                event = UpdatePasswordEvent.OnNewPasswordChange(it)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CltInput(
                        value = state.confirmNewPassword,
                        label = "Confirm password",
                        error = state.confirmNewPasswordError,
                        isPassword = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        onValueChange = {
                            updatePasswordViewModel.onEvent(
                                event = UpdatePasswordEvent.OnConfirmNewPasswordChange(it)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CltButton(
                        text = "Update Password",
                        withLoading = true,
                        enabled = !state.isSubmitting,
                        onClick = {
                            focusManager.clearFocus()
                            updatePasswordViewModel.onEvent(
                                event = UpdatePasswordEvent.OnSubmit
                            )
                        }
                    )
                }
            }
        }
    }
}