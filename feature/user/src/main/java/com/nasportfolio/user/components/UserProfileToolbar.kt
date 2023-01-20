package com.nasportfolio.user.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.*
import androidx.navigation.NavHostController
import com.nasportfolio.common.components.images.CltImageFromNetwork
import com.nasportfolio.common.components.loading.CltShimmer
import com.nasportfolio.common.components.typography.CltHeading
import com.nasportfolio.common.modifier.gradientBackground
import com.nasportfolio.common.theme.lightOrange
import com.nasportfolio.common.theme.mediumOrange

@OptIn(ExperimentalMotionApi::class)
@Composable
fun UserProfileToolbar(
    username: String,
    imageUrl: String?,
    progress: Float,
    arrowShown: Boolean,
    navController: NavHostController,
    appBarHeight: Dp,
    isCurrentUser: Boolean,
    uploadPhoto: () -> Unit,
    removePhoto: () -> Unit,
    isLoading: Boolean
) {
    val appbarCorner by remember(progress) {
        derivedStateOf { 0.dp + (progress * 50).dp }
    }
    val elevation by remember(progress) {
        derivedStateOf { 0.dp + (progress * 4).dp }
    }
    val usernameGradient by remember(progress) {
        derivedStateOf {
            Brush.linearGradient(
                colors = listOf(lightOrange, mediumOrange).map {
                    animateColorWithProgress(
                        startColor = Color.White,
                        endColor = it,
                        progress = progress
                    )
                }
            )
        }
    }
    var isMenuExpanded by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .height(height = appBarHeight)
            .zIndex(10f)
    ) {
        MotionLayout(
            modifier = Modifier.fillMaxWidth(),
            transition = getTransition(),
            start = startConstraintSet(
                arrowShown = arrowShown,
                isCurrentUser = isCurrentUser
            ),
            end = endConstraintSet(
                appBarHeight = appBarHeight,
                arrowShown = arrowShown,
                isCurrentUser = isCurrentUser
            ),
            progress = progress,
        ) {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        shape = RoundedCornerShape(
                            bottomStart = appbarCorner,
                            bottomEnd = appbarCorner
                        )
                    )
                    .layoutId(layoutId = "appbar"),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(alpha = progress)
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
            Surface(
                modifier = Modifier.layoutId(layoutId = "profile_pic"),
                elevation = elevation,
                shape = CircleShape,
                border = BorderStroke(
                    width = 2.dp,
                    color = Color.White.copy(
                        alpha = 1 - progress
                    )
                )
            ) {
                if (isLoading) CltShimmer(modifier = Modifier.fillMaxSize())
                if (!isLoading) Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    imageUrl?.let {
                        CltImageFromNetwork(
                            url = imageUrl,
                            placeholder = { CltShimmer(modifier = Modifier.fillMaxSize()) },
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
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
            if (isCurrentUser && !isLoading) Box(
                modifier = Modifier.layoutId("photo_edit_btn"),
            ) {
                Surface(
                    elevation = elevation * 2,
                    shape = RoundedCornerShape(5.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .clickable { isMenuExpanded = true }
                            .padding(5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            modifier = Modifier.gradientBackground(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        lightOrange,
                                        mediumOrange
                                    )
                                ),
                            )
                        )
                    }
                }
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false },
                ) {
                    DropdownMenuItem(
                        onClick = {
                            isMenuExpanded = false
                            uploadPhoto()
                        }
                    ) {
                        Text(text = "Upload a photo")
                    }
                    DropdownMenuItem(
                        enabled = imageUrl != null,
                        onClick = {
                            isMenuExpanded = false
                            removePhoto()
                        }
                    ) {
                        Text(text = "Remove photo")
                    }
                }
            }
            CltHeading(
                modifier = Modifier
                    .layoutId(layoutId = "username")
                    .gradientBackground(brush = usernameGradient),
                text = username,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )
            if (arrowShown) Box(
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
        }
    }
}

@Composable
private fun startConstraintSet(
    arrowShown: Boolean,
    isCurrentUser: Boolean
) = ConstraintSet {
    val profilePicRef = createRefFor("profile_pic")
    val appBarRef = createRefFor("appbar")
    val usernameRef = createRefFor("username")
    val backArrowRef = createRefFor("arrow")
    val photoEditBtn = createRefFor("photo_edit_btn")

    constrain(profilePicRef) {
        width = Dimension.value(40.dp)
        height = Dimension.value(40.dp)
        top.linkTo(parent.top, 8.dp)
        start.linkTo(if (arrowShown) backArrowRef.end else parent.start, 16.dp)
    }
    constrain(usernameRef) {
        top.linkTo(profilePicRef.top)
        bottom.linkTo(profilePicRef.bottom)
        start.linkTo(profilePicRef.end, 16.dp)
    }
    constrain(appBarRef) {
        width = Dimension.fillToConstraints
        height = Dimension.value(56.dp)
        top.linkTo(parent.top)
        centerHorizontallyTo(parent)
    }
    if (arrowShown) {
        constrain(backArrowRef) {
            width = Dimension.value(24.dp)
            height = Dimension.value(24.dp)
            centerVerticallyTo(profilePicRef)
            start.linkTo(parent.start, 16.dp)
        }
    }
    if (isCurrentUser) {
        constrain(photoEditBtn) {
            end.linkTo(profilePicRef.end, 4.dp)
            bottom.linkTo(profilePicRef.bottom, 4.dp)
            visibility = Visibility.Gone
        }
    }
}

@Composable
private fun endConstraintSet(
    appBarHeight: Dp,
    arrowShown: Boolean,
    isCurrentUser: Boolean
) = ConstraintSet {
    val profilePicRef = createRefFor("profile_pic")
    val appBarRef = createRefFor("appbar")
    val usernameRef = createRefFor("username")
    val backArrowRef = createRefFor("arrow")
    val photoEditBtn = createRefFor("photo_edit_btn")

    constrain(profilePicRef) {
        width = Dimension.value(125.dp)
        height = Dimension.value(125.dp)
        top.linkTo(appBarRef.bottom, (-60).dp)
        centerHorizontallyTo(parent)
    }
    constrain(usernameRef) {
        top.linkTo(profilePicRef.bottom, 16.dp)
        centerHorizontallyTo(profilePicRef)
    }
    constrain(appBarRef) {
        width = Dimension.fillToConstraints
        height = Dimension.value(appBarHeight * 0.5f)
        top.linkTo(parent.top)
        centerHorizontallyTo(parent)
    }
    if (arrowShown) {
        constrain(backArrowRef) {
            width = Dimension.value(24.dp)
            height = Dimension.value(24.dp)
            top.linkTo(parent.top, 16.dp)
            start.linkTo(parent.start, 16.dp)
        }
    }
    if (isCurrentUser) {
        constrain(photoEditBtn) {
            end.linkTo(profilePicRef.end, 4.dp)
            bottom.linkTo(profilePicRef.bottom, 4.dp)
            visibility = Visibility.Visible
        }
    }
}

@Composable
private fun getTransition() = Transition(
    content = """
      {
        from: "start",
        to: "end",
        pathMotionArc: "startHorizontal",
        KeyFrames: {
          KeyAttributes: [
            {
              target: ["username"],
              frames: [0, 50, 100],
              translationX: [0, 64, 0]
            },
            {
              target: ["photo_edit_btn"],
              frames: [0, 90, 100],
              alpha: [0, 0, 1]
            },
          ]
        }
      }
    """.trimIndent()
)

private fun animateColorWithProgress(
    startColor: Color,
    endColor: Color,
    progress: Float
): Color = startColor.copy(
    red = startColor.red - (progress * (startColor.red - endColor.red)),
    green = startColor.green - (progress * (startColor.green - endColor.green)),
    blue = startColor.blue - (progress * (startColor.blue - endColor.blue))
)
