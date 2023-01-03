package com.nasportfolio.restaurant.create.restaurant

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.border
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.nasportfolio.common.components.CltButton
import com.nasportfolio.common.components.CltImagePicker
import com.nasportfolio.common.components.CltInput
import com.nasportfolio.common.navigation.createRestaurantScreenRoute
import com.nasportfolio.common.navigation.navigateToCreateBranch
import com.nasportfolio.common.theme.mediumOrange
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CreateRestaurantScreen(
    navController: NavHostController,
    createRestaurantViewModel: CreateRestaurantViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val focusManager = LocalFocusManager.current

    val state by createRestaurantViewModel.state.collectAsState()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(true) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                createRestaurantViewModel.errorChannel.collect {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = it,
                        actionLabel = "Okay"
                    )
                }
            }
        }
    }

    LaunchedEffect(state.insertId) {
        state.insertId ?: return@LaunchedEffect
        navController.navigateToCreateBranch(
            restaurantId = state.insertId!!,
            popUpTo = createRestaurantScreenRoute
        )
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = "Create restaurant") },
                navigationIcon = {
                    IconButton(onClick = navController::popBackStack) {
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CltImagePicker(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .aspectRatio(1f)
                    .border(width = 2.dp, color = mediumOrange),
                value = state.image,
                onValueChange = {
                    createRestaurantViewModel.onEvent(
                        event = CreateRestaurantEvent.OnImageChanged(image = it)
                    )
                },
                error = state.imageError,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp,
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    CltInput(
                        value = state.name,
                        label = "Name",
                        error = state.nameError,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        onValueChange = {
                            createRestaurantViewModel.onEvent(
                                CreateRestaurantEvent.OnNameChanged(name = it)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CltInput(
                        value = state.description,
                        label = "Description",
                        error = state.descriptionError,
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        onValueChange = {
                            createRestaurantViewModel.onEvent(
                                CreateRestaurantEvent.OnDescriptionChanged(description = it)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    CltButton(
                        text = "Submit",
                        withLoading = true,
                        enabled = !state.isLoading,
                        onClick = {
                            focusManager.clearFocus()
                            createRestaurantViewModel.onEvent(
                                CreateRestaurantEvent.OnSubmit
                            )
                        }
                    )
                }
            }
        }
    }
}