package com.nasportfolio.clicktoeat.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nasportfolio.clicktoeat.R
import com.nasportfolio.clicktoeat.theme.FreeStyleScript
import com.nasportfolio.clicktoeat.theme.lightOrange
import com.nasportfolio.clicktoeat.theme.mediumOrange
import com.nasportfolio.clicktoeat.utils.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SplashScreen(
    navController: NavHostController
) {
    val heightOfScreen = with(LocalDensity.current) {
        LocalConfiguration.current.screenHeightDp.dp.toPx()
    }

    var canvasCenterY by remember {
        mutableStateOf(0f)
    }
    var showTitleAndLogo by remember {
        mutableStateOf(false)
    }

    val size = remember {
        Animatable(initialValue = 30f)
    }
    val fallingAnimation = remember {
        Animatable(initialValue = canvasCenterY)
    }

    LaunchedEffect(canvasCenterY) {
        if (canvasCenterY == 0f) return@LaunchedEffect
        fallingAnimation.animateTo(
            targetValue = canvasCenterY,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        launch {
            size.animateTo(
                targetValue = heightOfScreen * 2,
                animationSpec = tween(
                    durationMillis = 1000,
                    delayMillis = 500
                ),
            )
            delay(1000)
            navController.navigate(Screen.AuthScreen.route)
        }
        showTitleAndLogo = true
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            canvasCenterY = center.y
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(lightOrange, mediumOrange),
                ),
                radius = size.value,
                center = Offset(
                    x = center.x,
                    y = fallingAnimation.value
                )
            )
        }
        AnimatedVisibility(
            visible = showTitleAndLogo,
            enter = expandIn(
                expandFrom = Alignment.Center,
                animationSpec = tween(
                    durationMillis = 700,
                    delayMillis = 600
                )
            ),
            modifier = Modifier.size(500.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_clicktoeat_icon),
                    contentDescription = "Logo of ClickToEat",
                    modifier = Modifier.size(175.dp),
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "ClickToEat",
                    fontSize = MaterialTheme.typography.h2.fontSize,
                    color = Color.White,
                    fontFamily = FreeStyleScript
                )
            }
        }
    }

}