package com.nasportfolio.clicktoeat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CltInput(
    value: String,
    label: String,
    error: String?,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    var showPassword by remember {
        mutableStateOf(false)
    }
    val visualTransformation = getVisualTransformation(
        isPassword = isPassword,
        showPassword = showPassword
    )

    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            value = value,
            label = { Text(text = label) },
            onValueChange = onValueChange,
            isError = error != null,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            trailingIcon = {
                if (!isPassword) return@OutlinedTextField
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = getImageVector(
                            showPassword = showPassword,
                            isPassword = isPassword
                        ),
                        contentDescription = null
                    )
                }
            }
        )
        AnimatedVisibility(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            visible = error != null,
            enter = fadeIn() + slideInHorizontally(animationSpec = spring()),
        ) {
            error?.let {
                Text(
                    text = error,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.body1,
                )
            }
        }
    }
}

private fun getImageVector(
    isPassword: Boolean,
    showPassword: Boolean
): ImageVector {
    if (isPassword && showPassword)
        return Icons.Default.VisibilityOff
    return Icons.Default.Visibility
}

private fun getVisualTransformation(
    isPassword: Boolean,
    showPassword: Boolean
): VisualTransformation {
    if (isPassword && !showPassword)
        return PasswordVisualTransformation()
    return VisualTransformation.None
}