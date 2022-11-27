package com.nasportfolio.clicktoeat.data.restaurant

import com.nasportfolio.clicktoeat.data.branch.Branch
import com.nasportfolio.clicktoeat.data.common.Image

data class Restaurant(
    val id: String,
    val name: String,
    val description: String,
    val image: Image,
    val branches: List<Branch>
)
