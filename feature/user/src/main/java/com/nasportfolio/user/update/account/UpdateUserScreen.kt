package com.nasportfolio.user.update.account

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nasportfolio.common.components.buttons.CltButton
import com.nasportfolio.common.components.effects.CltLaunchFlowCollector
import com.nasportfolio.common.components.form.CltInput
import com.nasportfolio.common.components.navigation.BottomAppBarRefreshListener
import com.nasportfolio.common.navigation.navigateToUpdatePasswordScreen
import com.nasportfolio.common.navigation.navigateToUserProfile
import com.nasportfolio.common.navigation.userProfileScreen
import com.nasportfolio.user.update.account.components.ProfileImagePicker

@Composable
fun UpdateUserScreen(
    navController: NavHostController,
    updateUserViewModel: UpdateUserViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val focusManager = LocalFocusManager.current
    val activity = LocalContext.current as BottomAppBarRefreshListener
    val scaffoldState = rememberScaffoldState()
    val state by updateUserViewModel.state.collectAsState()

    CltLaunchFlowCollector(
        lifecycleOwner = lifecycleOwner,
        flow = updateUserViewModel.errorChannel
    ) {
        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
        scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
    }

    CltLaunchFlowCollector(
        lifecycleOwner = lifecycleOwner,
        flow = updateUserViewModel.updatedChannel
    ) {
        activity.refresh()
        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
        scaffoldState.snackbarHostState.showSnackbar(
            message = "User updated successfully!",
            actionLabel = "Okay"
        )
    }

    BackHandler(enabled = true) {
        navigateBack(
            state = state,
            navController = navController
        )
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = "Update user") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigateBack(
                                state = state,
                                navController = navController
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileImagePicker(
                state = state,
                updatePhoto = {
                    updateUserViewModel.onEvent(
                        event = UpdateUserEvent.OnImageChange(it)
                    )
                },
                deletePhoto = {
                    updateUserViewModel.onEvent(
                        event = UpdateUserEvent.OnRemoveImage
                    )
                }
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
                        value = state.username,
                        label = "Username",
                        error = state.usernameError,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        onValueChange = {
                            updateUserViewModel.onEvent(
                                event = UpdateUserEvent.OnUsernameChange(it)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CltInput(
                        value = state.email,
                        label = "Email",
                        error = state.emailError,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        onValueChange = {
                            updateUserViewModel.onEvent(
                                event = UpdateUserEvent.OnEmailChange(it)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CltButton(
                        text = "Update Account",
                        withLoading = true,
                        enabled = !state.isSubmitting,
                        onClick = {
                            focusManager.clearFocus()
                            updateUserViewModel.onEvent(
                                event = UpdateUserEvent.OnSubmit
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                CltButton(
                    modifier = Modifier.weight(1f),
                    text = "Edit Password",
                    withLoading = false,
                    enabled = true,
                    onClick = navController::navigateToUpdatePasswordScreen
                )
                Spacer(modifier = Modifier.width(5.dp))
                CltButton(
                    modifier = Modifier.weight(1f),
                    text = "Delete Account",
                    withLoading = false,
                    enabled = true,
                    gradient = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFE60000),
                            Color(0xFFFF5E5E)
                        )
                    ),
                    onClick = { /*TODO*/ }
                )
            }
        }
    }
}

private fun navigateBack(
    state: UpdateUserState,
    navController: NavHostController
) {
    if (!state.isUpdated) return run {
        navController.popBackStack()
    }
    state.userId?.let {
        navController.navigateToUserProfile(
            userId = it,
            fromNav = true,
            popUpTo = "$userProfileScreen/{userId}/{fromNav}"
        )
    }
}