package com.nasportfolio.restaurant.details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nasportfolio.common.components.CltHeading
import com.nasportfolio.common.utils.toStringAsFixed
import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.restaurant.TransformedRestaurant

@Composable
fun ReviewSection(restaurant: TransformedRestaurant) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            CltHeading(
                text = "Reviews",
                textAlign = TextAlign.Start,
                fontSize = 30.sp
            )
            if (restaurant.comments.isNotEmpty())
                TextButton(onClick = { /*TODO*/ }) {
                    Text(text = "See all")
                }
        }
        Spacer(modifier = Modifier.height(5.dp))
        if (restaurant.comments.isNotEmpty())
            Surface(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp,
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, bottom = 10.dp, top = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = buildAnnotatedString(restaurant = restaurant))
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))
                        repeat(5) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row {
                                    repeat(5 - it) {
                                        Icon(
                                            modifier = Modifier.size(10.dp),
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(5.dp))
                                LinearProgressIndicator(
                                    progress = calculatePercentOfUsers(
                                        comments = restaurant.comments,
                                        rating = 5 - it
                                    ),
                                    modifier = Modifier
                                        .width(150.dp)
                                        .clip(CircleShape),
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                        Text(
                            text = "${restaurant.ratingCount} reviews",
                            color = MaterialTheme.colors.onBackground.copy(
                                alpha = if (isSystemInDarkTheme()) 0.5f else 0.7f
                            )
                        )
                    }
                }
            }
        if (restaurant.comments.isEmpty()) EmptyReviews()
    }
}


@Composable
private fun EmptyReviews() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(2) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .offset(
                        x = if (it == 0) (-25).dp else 25.dp,
                        y = if (it == 0) 10.dp else (-10).dp
                    ),
                shape = RoundedCornerShape(10.dp),
                elevation = if (isSystemInDarkTheme()) {
                    if (it == 0) 4.dp else 20.dp
                } else {
                    if (it == 0) 10.dp else 15.dp
                },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(
                                color = MaterialTheme.colors.onBackground.copy(
                                    alpha = 0.3f
                                )
                            )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        repeat(2) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(if (it == 0) 1f else 0.7f)
                                    .height(15.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        color = MaterialTheme.colors.onBackground.copy(
                                            alpha = 0.2f
                                        )
                                    )
                            )
                            if (it == 0) Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                }
            }
        }
        Text(text = buildAnnotatedString {
            withStyle(ParagraphStyle(textAlign = TextAlign.Center)) {
                withStyle(SpanStyle(fontSize = 24.sp)) {
                    append("No reviews yet...\n")
                }
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colors.onBackground.copy(
                            alpha = if (isSystemInDarkTheme()) {
                                0.5f
                            } else {
                                0.7f
                            }
                        )
                    )
                ) {
                    append("Try adding one now!")
                }
            }
        })
    }
}

private fun calculatePercentOfUsers(comments: List<Comment>, rating: Int): Float {
    val groupedComments = comments.groupBy { it.rating }
    val countOfUsersWithRating = groupedComments[rating]?.size?.toFloat() ?: 0f
    return countOfUsersWithRating / comments.size
}

@Composable
private fun buildAnnotatedString(restaurant: TransformedRestaurant) =
    buildAnnotatedString {
        withStyle(
            ParagraphStyle(
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
            )
        ) {
            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 54.sp
                )
            ) {
                append(restaurant.averageRating.toStringAsFixed(1))
                append("\n")
            }
            withStyle(
                SpanStyle(
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.onBackground.copy(
                        alpha = if (isSystemInDarkTheme()) {
                            0.5f
                        } else {
                            0.7f
                        }
                    )
                )
            ) {
                append("out of 5")
            }
        }
    }