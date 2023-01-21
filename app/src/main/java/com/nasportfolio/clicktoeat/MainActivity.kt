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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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
                    val navController = rememberNavController()
                    val bottomPadding = rememberBottomBarPadding()
                    val userId by mainViewModel.userId.collectAsState()
                    val profileImage by mainViewModel.profileImage.collectAsState()

                    val listener = remember {
                        NavController.OnDestinationChangedListener { _, destination, _ ->
                            val navigationRoutes = BottomNavigationBarItem.values().map {
                                it.route
                            }
                            if (destination.route in navigationRoutes) mainViewModel.updateImage()
                        }
                    }

                    DisposableEffect(true) {
                        navController.addOnDestinationChangedListener(listener = listener)
                        onDispose {
                            navController.removeOnDestinationChangedListener(listener = listener)
                        }
                    }

                    Scaffold(
                        bottomBar = {
                            CltBottomBar(
                                navController = navController,
                                bottomPadding = bottomPadding,
                                profileImage = profileImage,
                                userId = userId
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