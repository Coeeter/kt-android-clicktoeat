package com.nasportfolio.clicktoeat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nasportfolio.common.components.effects.CltLaunchFlowCollector
import com.nasportfolio.common.components.navigation.BottomAppBarRefreshListener
import com.nasportfolio.common.components.navigation.BottomNavigationBarItem
import com.nasportfolio.common.components.navigation.CltBottomBar
import com.nasportfolio.common.components.navigation.rememberBottomBarPadding
import com.nasportfolio.common.theme.ClickToEatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity(), BottomAppBarRefreshListener {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClickToEatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val lifecycleOwner = LocalLifecycleOwner.current
                    val navController = rememberNavController()
                    val bottomPadding = rememberBottomBarPadding()
                    val scaffoldState = rememberScaffoldState()
                    val profileImage by mainViewModel.profileImage.collectAsState()
                    val currentBackStackEntry by navController.currentBackStackEntryAsState()

                    LaunchedEffect(currentBackStackEntry) {
                        val route = currentBackStackEntry?.destination?.route
                        val navigationRoutes = BottomNavigationBarItem.values().map {
                            it.route
                        }
                        if (route in navigationRoutes) mainViewModel.updateImage()
                    }

                    CltLaunchFlowCollector(
                        lifecycleOwner = lifecycleOwner,
                        flow = NotificationService.snackBarChannel
                    ) {
                        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                        scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
                    }

                    Scaffold(
                        scaffoldState = scaffoldState,
                        bottomBar = {
                            CltBottomBar(
                                navController = navController,
                                bottomPadding = bottomPadding,
                                profileImage = profileImage
                            )
                        }
                    ) {
                        NavGraph(
                            navController = navController,
                            paddingValues = PaddingValues(bottom = bottomPadding.value.dp)
                        )
                    }
                }
            }
        }
    }

    override fun refresh() = mainViewModel.updateImage()
}