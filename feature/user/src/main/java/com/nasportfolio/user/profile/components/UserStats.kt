package com.nasportfolio.user.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.nasportfolio.common.components.loading.CltShimmer
import com.nasportfolio.common.components.typography.CltHeading
import com.nasportfolio.common.utils.toStringAsFixed
import com.nasportfolio.user.profile.UserProfileState

@Composable
fun UserStats(state: UserProfileState) {
    Surface(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
    ) {
        if (!state.isUserLoading && !state.isRestaurantLoading && !state.isCommentLoading) Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp, horizontal = 20.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CltHeading(text = state.comments.size.toString())
                    Text(text = "Reviews")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CltHeading(
                        text = buildAnnotatedString {
                            if (state.comments.isEmpty()) return@buildAnnotatedString append("0.0")
                            val totalRating = state.comments.sumOf {
                                it.rating.toDouble()
                            }
                            val size = state.comments.size.toDouble()
                            val average = totalRating / size
                            append(average.toStringAsFixed(1))
                        }
                    )
                    Text(text = "AVG Rating")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CltHeading(text = state.favRestaurants.size.toString())
                    Text(text = "Favorites")
                }
            }
        }
        if (state.isRestaurantLoading || state.isCommentLoading || state.isUserLoading) Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(3) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CltShimmer(
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    CltShimmer(
                        modifier = Modifier
                            .width(60.dp)
                            .height(20.dp)
                    )
                }
            }
        }
    }
}