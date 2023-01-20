package com.nasportfolio.search.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.nasportfolio.common.components.images.CltImageFromNetwork
import com.nasportfolio.common.components.loading.CltShimmer
import com.nasportfolio.common.theme.mediumOrange
import com.nasportfolio.domain.user.User
import com.nasportfolio.search.SearchScreenState

@Composable
fun SearchUserCard(
    modifier: Modifier = Modifier,
    user: User,
    state: SearchScreenState,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            user.image?.url?.let {
                CltImageFromNetwork(
                    url = it,
                    placeholder = { CltShimmer() },
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .border(width = 2.dp, color = mediumOrange, shape = CircleShape)
                )
            } ?: Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(width = 2.dp, color = mediumOrange, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = buildAnnotatedString {
                    if (state.query.isEmpty()) {
                        return@buildAnnotatedString append(user.username)
                    }
                    val start = user.username.lowercase().indexOf(
                        state.query.lowercase()
                    )
                    val end = start + state.query.length
                    user.username.forEachIndexed { index, char ->
                        if (index in start until end) return@forEachIndexed withStyle(
                            style = SpanStyle(color = mediumOrange)
                        ) {
                            append(char)
                        }
                        append(char)
                    }
                },
                style = MaterialTheme.typography.h6
            )
        }
    }
}