package com.nasportfolio.search.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.nasportfolio.common.components.images.CltImageFromNetwork
import com.nasportfolio.common.components.loading.CltShimmer
import com.nasportfolio.common.modifier.gradientBackground
import com.nasportfolio.common.theme.lightOrange
import com.nasportfolio.common.theme.mediumOrange
import com.nasportfolio.domain.restaurant.TransformedRestaurant
import com.nasportfolio.search.SearchScreenState

@Composable
fun SearchRestaurantCard(
    modifier: Modifier = Modifier,
    restaurant: TransformedRestaurant,
    state: SearchScreenState,
    onFavBtnClicked: () -> Unit,
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CltImageFromNetwork(
                    url = restaurant.imageUrl,
                    placeholder = { CltShimmer() },
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .border(width = 2.dp, color = mediumOrange)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = buildAnnotatedString {
                        if (state.query.isEmpty()) {
                            return@buildAnnotatedString append(restaurant.name)
                        }
                        val start = restaurant.name.lowercase().indexOf(
                            state.query.lowercase()
                        )
                        val end = start + state.query.length
                        restaurant.name.forEachIndexed { index, char ->
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
            IconButton(onClick = onFavBtnClicked) {
                val isFavorited = restaurant.favoriteUsers
                    .map { it.id }
                    .contains(state.currentLoggedInUser?.id)

                Icon(
                    imageVector = if (isFavorited) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                    contentDescription = null,
                    modifier = Modifier.gradientBackground(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                lightOrange,
                                mediumOrange
                            )
                        )
                    )
                )
            }
        }
    }
}