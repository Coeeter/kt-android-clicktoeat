package com.nasportfolio.common.components.typography

import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.nasportfolio.common.modifier.gradientBackground
import com.nasportfolio.common.theme.lightOrange
import com.nasportfolio.common.theme.mediumOrange

@Composable
fun CltHeading(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign = TextAlign.Center,
    fontWeight: FontWeight = FontWeight.Bold,
    fontSize: TextUnit = 25.sp,
    color: Color = mediumOrange
) {
    Box(modifier = modifier) {
        Text(
            modifier = Modifier.gradientBackground(
                brush = Brush.linearGradient(
                    colors = listOf(lightOrange, mediumOrange)
                )
            ),
            text = text,
            textAlign = textAlign,
            fontWeight = fontWeight,
            style = MaterialTheme.typography.h5.copy(
                color = color,
                fontSize = fontSize
            )
        )
    }
}