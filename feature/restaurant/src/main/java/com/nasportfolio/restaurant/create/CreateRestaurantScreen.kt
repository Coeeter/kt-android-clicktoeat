package com.nasportfolio.restaurant.create

import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.nasportfolio.common.components.CltInput
import com.nasportfolio.common.navigation.createRestaurantScreenRoute
import com.nasportfolio.common.navigation.navigateToHomeScreen
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
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scaffoldState = rememberScaffoldState()
    val state by createRestaurantViewModel.state.collectAsState()
    var bitmap by remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            bitmap = ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(
                    context.contentResolver,
                    uri!!
                )
            ).asImageBitmap()
            return@rememberLauncherForActivityResult
        }
        bitmap = MediaStore.Images.Media.getBitmap(
            context.contentResolver,
            uri
        ).asImageBitmap()
    }

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

    LaunchedEffect(bitmap) {
        bitmap ?: return@LaunchedEffect
        createRestaurantViewModel.onEvent(
            CreateRestaurantEvent.OnImageChanged(
                image = bitmap!!.asAndroidBitmap()
            )
        )
    }

    LaunchedEffect(state.isCreated) {
        if (!state.isCreated) return@LaunchedEffect
        navController.navigateToHomeScreen(
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
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .aspectRatio(1f)
                    .border(width = 2.dp, color = mediumOrange),
                elevation = 10.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    bitmap?.let {
                        Image(
                            bitmap = it,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } ?: Icon(
                        modifier = Modifier.fillMaxSize(0.5f),
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = mediumOrange
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            CltButton(
                modifier = Modifier.fillMaxWidth(0.6f),
                text = bitmap?.let { "Change picture" } ?: "Choose picture",
                withLoading = true,
                enabled = true,
                onClick = { pickImage.launch("image/*") }
            )
            AnimatedVisibility(
                visible = state.imageError != null,
                enter = fadeIn() + slideInHorizontally(animationSpec = spring()),
            ) {
                state.imageError?.let {
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