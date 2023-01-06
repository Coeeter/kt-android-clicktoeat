package com.nasportfolio.domain.restaurant

import com.nasportfolio.domain.branch.Branch
import com.nasportfolio.domain.image.Image

data class Restaurant(
    val id: String,
    val name: String,
    val description: String,
    val image: Image,
    val branches: List<Branch> = emptyList()
)
