package com.nasportfolio.restaurant.create.branch

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun CreateBranchScreen(
    navController: NavHostController,
    createBranchViewModel: CreateBranchViewModel = hiltViewModel()
) {
    val state by createBranchViewModel.state.collectAsState()

    Text(text = state.restaurantId!!)
}