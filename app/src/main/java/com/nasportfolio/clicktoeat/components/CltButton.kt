package com.nasportfolio.clicktoeat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.nasportfolio.clicktoeat.theme.lightOrange
import com.nasportfolio.clicktoeat.theme.mediumOrange

@Composable
fun CltButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    shape: Shape = RoundedCornerShape(10.dp),
    enabled: Boolean = true,
    disabledColor: Color = Color.LightGray,
    gradient: Brush = Brush.horizontalGradient(
        colors = listOf(lightOrange, mediumOrange)
    ),
    child: @Composable () -> Unit,
) {
    var boxModifier = Modifier.fillMaxWidth()
    if (enabled) boxModifier = boxModifier.background(gradient, shape = shape)
    if (!enabled) boxModifier = boxModifier.background(disabledColor, shape = shape)

    Button(
        modifier = modifier,
        enabled = enabled,
        contentPadding = PaddingValues(0.dp),
        onClick = onClick,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
        ),
    ) {
        Box(
            modifier = boxModifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .then(modifier),
            contentAlignment = Alignment.Center
        ) {
            child()
        }
    }
}