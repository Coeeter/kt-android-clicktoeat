package com.nasportfolio.common.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nasportfolio.common.components.images.CltImageFromNetwork
import com.nasportfolio.common.components.loading.CltShimmer
import com.nasportfolio.common.modifier.gradientBackground
import com.nasportfolio.common.theme.lightOrange
import com.nasportfolio.common.theme.mediumOrange
import com.nasportfolio.common.utils.toStringAsFixed
import com.nasportfolio.domain.restaurant.TransformedRestaurant

@Composable
fun CltRestaurantCard(
    modifier: Modifier = Modifier,
    restaurant: TransformedRestaurant,
    toggleFavorite: (String) -> Unit,
    onClick: (String) -> Unit,
) {
    Card(
        modifier = modifier
            .padding(5.dp)
            .clickable {
                onClick(restaurant.id)
            },
        elevation = 4.dp
    ) {
        Column {
            CltImageFromNetwork(
                url = restaurant.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .border(width = 2.dp, color = mediumOrange),
                placeholder = {
                    CltShimmer(modifier = Modifier.fillMaxSize())
                },
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, bottom = 10.dp, start = 10.dp, end = 10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = restaurant.name,
                        style = MaterialTheme.typography.h6,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        modifier = Modifier
                            .offset(x = 5.dp)
                            .clip(CircleShape)
                            .size(40.dp),
                        onClick = { toggleFavorite(restaurant.id) }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getFavoriteIcon(restaurant.isFavoriteByCurrentUser),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = mediumOrange
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = restaurant.averageRating.toStringAsFixed(1),
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "/5")
                    Text(text = "(${restaurant.ratingCount})")
                }
            }
        }
    }
}

private fun getFavoriteIcon(isFavorited: Boolean): ImageVector {
    if (isFavorited) return Icons.Default.Favorite
    return Icons.Default.FavoriteBorder
}