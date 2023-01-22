package com.nasportfolio.restaurant.likedislike.components

import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nasportfolio.common.components.loading.CltShimmer
import com.nasportfolio.common.theme.mediumOrange
import com.nasportfolio.restaurant.likedislike.TabItem

@Composable
fun EmptyItems(tabItem: TabItem) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        repeat(2) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = if (it == 0) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .offset(y = if (it == 0) 15.dp else (-15).dp),
                    elevation = if (isSystemInDarkTheme()) {
                        if (it == 0) 4.dp else 20.dp
                    } else {
                        if (it == 0) 4.dp else 10.dp
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CltShimmer(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 2.dp,
                                    color = mediumOrange,
                                    shape = CircleShape
                                ),
                            lightModeHighlightColor = Color.LightGray.copy(0.3f),
                            darkModeHighlightColor = Color.LightGray.copy(0.3f),
                            darkModeBaseColor = Color.LightGray.copy(0.3f)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        CltShimmer(
                            modifier = Modifier
                                .width(150.dp)
                                .height(25.dp),
                            lightModeHighlightColor = Color.LightGray.copy(0.3f),
                            darkModeHighlightColor = Color.LightGray.copy(0.3f),
                            darkModeBaseColor = Color.LightGray.copy(0.3f)
                        )
                    }
                }
            }
        }
        Text(
            text = "No ${tabItem.title.lowercase()} yet...",
            style = MaterialTheme.typography.h6
        )
    }
}