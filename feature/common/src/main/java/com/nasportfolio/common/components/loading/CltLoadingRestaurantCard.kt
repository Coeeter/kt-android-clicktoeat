package com.nasportfolio.common.components.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nasportfolio.common.theme.mediumOrange

@Composable
fun CltLoadingRestaurantCard(
    modifier: Modifier = Modifier,
    shimmer: Boolean = true,
    elevation: Dp = 4.dp
) {
    Card(
        modifier = modifier.padding(5.dp),
        elevation = elevation
    ) {
        Column {
            Component(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .border(width = 2.dp, color = mediumOrange),
                shimmer = shimmer
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
                    Component(
                        modifier = Modifier
                            .weight(1f)
                            .height(25.dp),
                        shimmer = shimmer
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Component(
                        modifier = Modifier
                            .fillMaxWidth(0.2f)
                            .aspectRatio(1f)
                            .clip(CircleShape),
                        shimmer = shimmer
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Component(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(25.dp),
                    shimmer = shimmer
                )
            }
        }
    }
}

@Composable
private fun Component(
    modifier: Modifier,
    shimmer: Boolean
) {
    val baseColor = if (isSystemInDarkTheme()) {
        Color.LightGray.copy(0.1f)
    } else {
        Color.LightGray.copy(0.3f)
    }

    if (shimmer) {
        CltShimmer(modifier = modifier)
    } else {
        Box(modifier = modifier.background(baseColor))
    }
}