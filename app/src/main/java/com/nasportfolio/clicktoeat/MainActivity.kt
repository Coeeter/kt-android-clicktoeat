package com.nasportfolio.clicktoeat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.nasportfolio.common.components.navigation.CltBottomBar
import com.nasportfolio.common.components.navigation.rememberBottomBarPadding
import com.nasportfolio.common.theme.ClickToEatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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

                    Scaffold(
                        bottomBar = {
                            CltBottomBar(
                                navController = navController,
                                bottomPadding = bottomPadding
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
}