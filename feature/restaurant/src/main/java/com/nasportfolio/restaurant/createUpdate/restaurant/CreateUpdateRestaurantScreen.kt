package com.nasportfolio.restaurant.createUpdate.restaurant

import android.annotation.SuppressLint
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nasportfolio.common.components.buttons.CltButton
import com.nasportfolio.common.components.effects.CltLaunchFlowCollector
import com.nasportfolio.common.components.form.CltImagePicker
import com.nasportfolio.common.components.form.CltInput
import com.nasportfolio.common.navigation.createUpdateRestaurantScreenRoute
import com.nasportfolio.common.navigation.navigateToCreateBranch
import com.nasportfolio.common.navigation.navigateToRestaurantDetails
import com.nasportfolio.common.navigation.restaurantDetailScreenRoute
import com.nasportfolio.common.theme.mediumOrange
import com.nasportfolio.test.tags.TestTags

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CreateUpdateRestaurantScreen(
    navController: NavHostController,
    createUpdateRestaurantViewModel: CreateUpdateRestaurantViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val focusManager = LocalFocusManager.current

    val state by createUpdateRestaurantViewModel.state.collectAsState()
    val scaffoldState = rememberScaffoldState()

    CltLaunchFlowCollector(
        lifecycleOwner = lifecycleOwner,
        flow = createUpdateRestaurantViewModel.errorChannel
    ) {
        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
        scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
    }

    LaunchedEffect(state.insertId, state.isUpdateComplete) {
        state.insertId ?: return@LaunchedEffect
        if (state.isUpdateForm) {
            if (state.isUpdateComplete)
                navController.navigateToRestaurantDetails(
                    restaurantId = state.insertId!!,
                    popUpTo = "$restaurantDetailScreenRoute/{restaurantId}"
                )
            return@LaunchedEffect
        }
        navController.navigateToCreateBranch(
            restaurantId = state.insertId!!,
            popUpTo = createUpdateRestaurantScreenRoute
        )
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = if (state.isUpdateForm) "Update restaurant" else "Create restaurant") },
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
                    createUpdateRestaurantViewModel.onEvent(
                        event = CreateUpdateRestaurantEvent.OnImageChanged(image = it)
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
                        testTag = TestTags.RESTAURANT_NAME_INPUT,
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
                            createUpdateRestaurantViewModel.onEvent(
                                CreateUpdateRestaurantEvent.OnNameChanged(name = it)
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
                            createUpdateRestaurantViewModel.onEvent(
                                CreateUpdateRestaurantEvent.OnDescriptionChanged(description = it)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    CltButton(
                        modifier = Modifier.testTag(TestTags.UPDATE_RESTAURANT_BTN),
                        text = "Submit",
                        withLoading = true,
                        enabled = !state.isLoading,
                        onClick = {
                            focusManager.clearFocus()
                            createUpdateRestaurantViewModel.onEvent(
                                CreateUpdateRestaurantEvent.OnSubmit
                            )
                        }
                    )
                }
            }
        }
    }
}