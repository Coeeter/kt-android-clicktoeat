package com.nasportfolio.user.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.navigation.NavHostController
import com.nasportfolio.common.components.images.CltImageFromNetwork
import com.nasportfolio.common.components.loading.CltShimmer
import com.nasportfolio.common.modifier.gradientBackground
import com.nasportfolio.common.theme.lightOrange
import com.nasportfolio.common.theme.mediumOrange
import com.nasportfolio.user.R

@OptIn(ExperimentalMotionApi::class)
@Composable
fun UserProfileToolbar(
    username: String,
    imageUrl: String?,
    progress: Float,
    arrowShown: Boolean,
    navController: NavHostController,
    height: Dp,
) {
    val context = LocalContext.current
    val motionScene = rememberSaveable {
        context.resources
            .openRawResource(
                if (arrowShown) {
                    R.raw.user_profile_with_arrow_motion_scene
                } else {
                    R.raw.user_profile_motion_scene
                }
            )
            .readBytes()
            .decodeToString()
    }

    Box(
        modifier = Modifier
            .height(height = height)
            .zIndex(10f)
    ) {
        MotionLayout(
            modifier = Modifier.fillMaxWidth(),
            motionScene = MotionScene(content = motionScene),
            progress = progress,
        ) {
            val profilePicProperties by motionProperties(id = "profile_pic")
            val appBarProperties by motionProperties(id = "app_bar")
            val usernameProperties by motionProperties(id = "username")

            var appBarAlpha = appBarProperties.float("alpha")
            var appBarCorner = appBarProperties.int("corners").dp
            var profilePicElevation = profilePicProperties.int("elevation")
            var profilePicBorderColor = profilePicProperties.color("color")
            var gradientFirstColor = usernameProperties.color("first_color")
            var gradientSecondColor = usernameProperties.color("second_color")

            if (progress == 0f) {
                gradientFirstColor = Color.White
                gradientSecondColor = Color.White
                appBarCorner = 0.dp
                appBarAlpha = 0f
                profilePicElevation = 0
                profilePicBorderColor = Color.White
            }
            if (progress == 1f) {
                gradientFirstColor = lightOrange
                gradientSecondColor = mediumOrange
                appBarCorner = 50.dp
                appBarAlpha = 1f
                profilePicElevation = 4
                profilePicBorderColor = Color.Transparent
            }

            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        shape = RoundedCornerShape(
                            bottomStart = appBarCorner,
                            bottomEnd = appBarCorner
                        )
                    )
                    .layoutId(appBarProperties.id()),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(alpha = appBarAlpha)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    lightOrange,
                                    mediumOrange
                                ),
                            )
                        )
                )
            }
            if (arrowShown)
                Box(
                    modifier = Modifier.layoutId("arrow"),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            Surface(
                modifier = Modifier.layoutId(profilePicProperties.id()),
                elevation = profilePicElevation.dp,
                shape = CircleShape,
                border = BorderStroke(
                    width = 2.dp,
                    color = profilePicBorderColor
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    imageUrl?.let {
                        CltImageFromNetwork(
                            url = imageUrl,
                            placeholder = { CltShimmer() },
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    } ?: Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp)
                    )
                }
            }
            Text(
                modifier = Modifier
                    .layoutId(usernameProperties.id())
                    .gradientBackground(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                gradientFirstColor,
                                gradientSecondColor
                            )
                        )
                    ),
                text = username,
                style = MaterialTheme.typography.h6,
            )
        }
    }
}