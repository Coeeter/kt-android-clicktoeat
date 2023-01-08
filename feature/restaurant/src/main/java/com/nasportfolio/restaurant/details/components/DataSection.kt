package com.nasportfolio.restaurant.details.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nasportfolio.common.components.CltHeading
import com.nasportfolio.common.utils.toStringAsFixed
import com.nasportfolio.domain.restaurant.TransformedRestaurant


@Composable
fun DataSection(restaurant: TransformedRestaurant) {
    Surface(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CltHeading(text = restaurant.averageRating.toStringAsFixed(1))
                Text(text = "AVG Rating")
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CltHeading(text = restaurant.ratingCount.toString())
                Text(text = "Reviews")
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CltHeading(text = restaurant.favoriteSize.toString())
                Text(text = "Favorites")
            }
        }
    }
}