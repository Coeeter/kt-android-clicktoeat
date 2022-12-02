package com.nasportfolio.clicktoeat.domain.restaurant

import com.nasportfolio.clicktoeat.domain.branch.Branch
import com.nasportfolio.clicktoeat.domain.common.Image

data class Restaurant(
    val id: String,
    val name: String,
    val description: String,
    val image: Image,
    val branches: List<Branch> = emptyList()
)
