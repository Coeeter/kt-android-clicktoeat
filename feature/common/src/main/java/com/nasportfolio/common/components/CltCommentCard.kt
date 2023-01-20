package com.nasportfolio.common.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nasportfolio.common.components.images.CltImageFromNetwork
import com.nasportfolio.common.components.loading.CltShimmer
import com.nasportfolio.common.components.typography.CltHeading
import com.nasportfolio.common.navigation.navigateToRestaurantDetails
import com.nasportfolio.common.navigation.navigateToUserProfile
import com.nasportfolio.common.theme.mediumOrange
import com.nasportfolio.domain.comment.Comment
import java.text.SimpleDateFormat
import java.util.*

enum class TopBar {
    User,
    Restaurant
}

@Composable
fun CltCommentCard(
    modifier: Modifier = Modifier,
    topBar: TopBar = TopBar.User,
    navController: NavHostController,
    comment: Comment,
    currentUserId: String,
    editComment: () -> Unit,
    deleteComment: () -> Unit
) {
    var isMenuExpanded by remember {
        mutableStateOf(false)
    }

    Surface(
        modifier = modifier,
        elevation = 4.dp,
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.clickable {
                        when (topBar) {
                            TopBar.Restaurant -> navController.navigateToRestaurantDetails(
                                restaurantId = comment.restaurant.id
                            )
                            TopBar.User -> navController.navigateToUserProfile(
                                userId = comment.user.id
                            )
                        }
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (topBar == TopBar.User)
                        comment.user.image?.url?.let {
                            CltImageFromNetwork(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.dp,
                                        color = mediumOrange,
                                        shape = CircleShape
                                    ),
                                url = it,
                                contentScale = ContentScale.Crop,
                                placeholder = { CltShimmer() },
                                contentDescription = null
                            )
                        } ?: Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(width = 2.dp, color = mediumOrange, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null
                            )
                        }
                    if (topBar == TopBar.Restaurant) CltImageFromNetwork(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(
                                width = 2.dp,
                                color = mediumOrange,
                                shape = CircleShape
                            ),
                        url = comment.restaurant.image.url,
                        placeholder = { CltShimmer() },
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    CltHeading(
                        text = when (topBar) {
                            TopBar.User -> comment.user.username
                            TopBar.Restaurant -> comment.restaurant.name
                        },
                        fontSize = 20.sp
                    )
                }
                if (currentUserId == comment.user.id && topBar != TopBar.Restaurant) {
                    Box(contentAlignment = Alignment.Center) {
                        IconButton(
                            modifier = Modifier.offset(x = 10.dp),
                            onClick = { isMenuExpanded = true }
                        ) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = isMenuExpanded,
                            onDismissRequest = { isMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    isMenuExpanded = false
                                    editComment()
                                }
                            ) {
                                Text(text = "Edit Comment")
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            DropdownMenuItem(
                                onClick = {
                                    isMenuExpanded = false
                                    deleteComment()
                                }
                            ) {
                                Text(text = "Delete Comment")
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = comment.review)
            Spacer(modifier = Modifier.height(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = mediumOrange
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = comment.rating.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(text = "/5", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = SimpleDateFormat(
                        "dd-MM-yyyy HH:mm:ss",
                        Locale.ENGLISH
                    ).format(
                        if (comment.createdAt == comment.updatedAt) {
                            comment.createdAt
                        } else {
                            comment.updatedAt
                        }
                    ),
                    color = MaterialTheme.colors.onBackground.copy(
                        alpha = if (isSystemInDarkTheme()) {
                            0.5f
                        } else {
                            0.7f
                        }
                    )
                )
                Spacer(modifier = Modifier.width(5.dp))
                if (comment.createdAt.time != comment.updatedAt.time)
                    Text(
                        text = "(Edited)",
                        color = MaterialTheme.colors.onBackground.copy(
                            alpha = if (isSystemInDarkTheme()) {
                                0.5f
                            } else {
                                0.7f
                            }
                        )
                    )
            }
        }
    }
}