package com.nasportfolio.clicktoeat.screens.home.components

import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nasportfolio.common.components.CltImageFromNetwork
import com.nasportfolio.common.theme.mediumOrange
import com.nasportfolio.domain.restaurant.TransformedRestaurant

@Composable
fun RestaurantCard(
    restaurant: TransformedRestaurant,
    toggleFavorite: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        elevation = 4.dp
    ) {
        Column {
            CltImageFromNetwork(
                url = restaurant.imageUrl,
                placeholder = {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                },
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .border(width = 2.dp, color = mediumOrange)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, bottom = 10.dp, start = 10.dp, end = 10.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
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
                                tint = mediumOrange,
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
                        text = restaurant.averageRating.toString(),
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