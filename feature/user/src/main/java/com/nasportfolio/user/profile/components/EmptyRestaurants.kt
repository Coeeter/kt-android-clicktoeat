package com.nasportfolio.user.profile.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nasportfolio.common.components.loading.CltLoadingRestaurantCard

@Composable
fun EmptyRestaurants() {
    val config = LocalConfiguration.current
    val width = remember {
        ((config.screenWidthDp - 30) / 2).dp
    }

    Column(
        modifier = Modifier.width(config.screenWidthDp.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Row {
            repeat(2) {
                CltLoadingRestaurantCard(
                    modifier = Modifier
                        .width(width)
                        .offset(
                            x = if (it == 0) 60.dp else (-60).dp,
                            y = if (it == 0) (-20).dp else 20.dp
                        ),
                    shimmer = false,
                    elevation = if (isSystemInDarkTheme()) {
                        if (it == 0) 4.dp else 20.dp
                    } else {
                        if (it == 0) 10.dp else 12.dp
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "No favorite restaurants yet...",
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}