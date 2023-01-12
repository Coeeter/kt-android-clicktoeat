package com.nasportfolio.restaurant.createUpdate.branch

import android.view.MotionEvent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.nasportfolio.common.components.CltButton
import com.nasportfolio.common.components.CltInput
import com.nasportfolio.common.navigation.*
import com.nasportfolio.common.theme.mediumOrange
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateUpdateBranchScreen(
    navController: NavHostController,
    createUpdateBranchViewModel: CreateUpdateBranchViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by createUpdateBranchViewModel.state.collectAsState()

    val scaffoldState = rememberScaffoldState()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(1.35, 103.87),
            10f
        )
    }
    var isScrollEnabled by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(true) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                createUpdateBranchViewModel.errorChannel.collect {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                    scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
                }
            }
        }
    }

    LaunchedEffect(state.isError, state.isCreated) {
        if (!state.isError && !state.isCreated) return@LaunchedEffect
        navController.navigateToHomeScreen(
            popUpTo = homeScreenRoute
        )
    }

    LaunchedEffect(state.isUpdated) {
        if (!state.isUpdated) return@LaunchedEffect
        state.restaurantId?.let {
            navController.navigateToRestaurantDetails(
                restaurantId = it,
                popUpTo = "$restaurantDetailScreenRoute/{restaurantId}"
            )
        }
    }

    LaunchedEffect(state.latLng) {
        state.latLng?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    it,
                    16f
                )
            )
        }
    }

    BackHandler(enabled = true) {
        if (state.isUpdateForm) {
            if (state.isUpdated) {
                state.restaurantId?.let {
                    navController.navigateToRestaurantDetails(
                        restaurantId = it,
                        popUpTo = "$restaurantDetailScreenRoute/{restaurantId}"
                    )
                }
                return@BackHandler
            }
            navController.popBackStack()
            return@BackHandler
        }
        navController.navigateToHomeScreen(
            popUpTo = "$createUpdateBranchScreenRoute/{restaurantId}/{branchId}"
        )
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.branchId?.let {
                            "Update branch"
                        } ?: "Add branch to restaurant"
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (state.isUpdateForm) {
                                if (state.isUpdated) {
                                    state.restaurantId?.let {
                                        navController.navigateToRestaurantDetails(
                                            restaurantId = it,
                                            popUpTo = "$restaurantDetailScreenRoute/{restaurantId}"
                                        )
                                    }
                                    return@IconButton
                                }
                                navController.popBackStack()
                                return@IconButton
                            }
                            navController.navigateToHomeScreen(
                                popUpTo = homeScreenRoute
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
                .verticalScroll(rememberScrollState(), enabled = isScrollEnabled)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .border(width = 2.dp, color = mediumOrange)
                    .motionEventSpy {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                isScrollEnabled = false
                            }
                            MotionEvent.ACTION_UP -> {
                                isScrollEnabled = true
                            }
                        }
                    },
                cameraPositionState = cameraPositionState,
                onMapClick = {
                    createUpdateBranchViewModel.onEvent(
                        CreateUpdateBranchEvent.OnLocationChanged(latLng = it)
                    )
                }
            ) {
                state.latLng?.let {
                    Marker(
                        state = MarkerState(position = it)
                    )
                }
            }
            AnimatedVisibility(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                visible = state.latLngError != null,
                enter = fadeIn() + slideInHorizontally(animationSpec = spring()),
            ) {
                state.latLngError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.body1,
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp,
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    CltInput(
                        value = state.address,
                        label = "Name",
                        error = state.addressError,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        onValueChange = {
                            createUpdateBranchViewModel.onEvent(
                                CreateUpdateBranchEvent.OnAddressChanged(address = it)
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
                            createUpdateBranchViewModel.onEvent(
                                CreateUpdateBranchEvent.OnSubmit
                            )
                        }
                    )
                }
            }
        }
    }
}