package com.nasportfolio.search.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nasportfolio.common.components.loading.CltShimmer
import com.nasportfolio.common.theme.mediumOrange

@Composable
fun RestaurantLoadingCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 10.dp,
                    top = 10.dp,
                    bottom = 10.dp,
                    end = 18.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CltShimmer(
                    modifier = Modifier
                        .size(50.dp)
                        .border(width = 2.dp, color = mediumOrange)
                )
                Spacer(modifier = Modifier.width(10.dp))
                CltShimmer(
                    modifier = Modifier
                        .width(150.dp)
                        .height(25.dp)
                )
            }
            CltShimmer(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
            )
        }
    }
}