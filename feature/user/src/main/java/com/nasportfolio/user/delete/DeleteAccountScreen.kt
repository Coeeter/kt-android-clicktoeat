package com.nasportfolio.user.delete

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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.nasportfolio.common.navigation.deleteUserScreen
import com.nasportfolio.common.navigation.navigateToAuthScreen

@Composable
fun DeleteAccountScreen(
    navController: NavHostController,
    deleteAccountViewModel: DeleteAccountViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val focusManager = LocalFocusManager.current
    val scaffoldState = rememberScaffoldState()
    val state by deleteAccountViewModel.state.collectAsState()
    val brush = remember {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFFF5E5E),
                Color(0xFFE60000)
            )
        )
    }
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    CltLaunchFlowCollector(
        lifecycleOwner = lifecycleOwner,
        flow = deleteAccountViewModel.errorChannel
    ) {
        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
        scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
    }

    LaunchedEffect(state.isDeleted) {
        if (!state.isDeleted) return@LaunchedEffect
        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
        scaffoldState.snackbarHostState.showSnackbar(
            message = "Deleted account successfully",
            actionLabel = "Okay"
        )
        navController.navigateToAuthScreen(
            popUpTo = deleteUserScreen
        )
    }

    BackHandler(enabled = true) {
        if (state.isDeleted) return@BackHandler
        navController.popBackStack()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = "Delete account") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (state.isDeleted) return@IconButton
                            navController.popBackStack()
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
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .gradientBackground(brush = brush),
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
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        onValueChange = {
                            deleteAccountViewModel.onPasswordChanged(it)
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CltButton(
                        text = "Delete Account",
                        withLoading = true,
                        enabled = !state.isLoading,
                        gradient = brush,
                        onClick = {
                            focusManager.clearFocus()
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }

        if (showDeleteDialog) AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(text = "Are you sure you want to delete your account?")
            },
            text = {
                Text(text = "This action is irreversible and will delete all data related to this account.")
            },
            buttons = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    CltButton(
                        modifier = Modifier.weight(1f),
                        text = "Cancel",
                        withLoading = false,
                        enabled = true,
                        onClick = {
                            showDeleteDialog = false
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    CltButton(
                        modifier = Modifier.weight(1f),
                        text = "Delete",
                        withLoading = false,
                        enabled = true,
                        gradient = Brush.horizontalGradient(
                            colors = listOf(Color(0xFFE60000), Color(0xFFFF5E5E))
                        ),
                        onClick = {
                            showDeleteDialog = false
                            deleteAccountViewModel.submit()
                        }
                    )
                }
            }
        )
    }
}