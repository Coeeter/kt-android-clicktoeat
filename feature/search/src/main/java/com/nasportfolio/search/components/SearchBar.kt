package com.nasportfolio.search.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.ImeAction
import com.nasportfolio.search.SearchScreenState
import com.nasportfolio.search.SearchScreenViewModel

@Composable
fun SearchBar(
    state: SearchScreenState,
    searchScreenViewModel: SearchScreenViewModel,
    focusManager: FocusManager
) {
    TextField(
        value = state.query,
        onValueChange = searchScreenViewModel::onQuery,
        label = { Text(text = "Search") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
            )
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = state.query.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    onClick = {
                        focusManager.clearFocus()
                        searchScreenViewModel.clearQuery()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                    )
                }
            }
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = if (isSystemInDarkTheme()) {
                MaterialTheme.colors.background.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colors.primary
            },
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
            trailingIconColor = Color.White.copy(alpha = 0.8f),
            leadingIconColor = Color.White.copy(alpha = 0.8f),
            textColor = Color.White,
            cursorColor = if (isSystemInDarkTheme()) {
                MaterialTheme.colors.primary
            } else {
                Color.White
            }
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                focusManager.clearFocus()
            }
        ),
        shape = RectangleShape,
        modifier = Modifier.fillMaxSize()
    )
}