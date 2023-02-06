package com.nasportfolio.data.restaurant.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.nasportfolio.data.branch.local.BranchEntity
import com.nasportfolio.domain.branch.Branch
import com.nasportfolio.domain.image.Image
import com.nasportfolio.domain.restaurant.Restaurant

@Entity(tableName = "restaurant")
data class RestaurantEntity(
    @PrimaryKey
    val restaurantId: String,
    val name: String,
    val description: String,
    @Embedded val image: Image
)

data class RestaurantWithBranches(
    @Embedded
    val restaurantEntity: RestaurantEntity?,
    @Relation(
        parentColumn = "id",
        entityColumn = "restaurant_id"
    )
    val branches: List<BranchEntity>
)

fun Restaurant.toRestaurantEntity() = RestaurantEntity(
    restaurantId = id,
    name = name,
    description = description,
    image = image
)

fun RestaurantWithBranches.toExternalRestaurant(): Restaurant? {
    restaurantEntity ?: return null
    val restaurant = Restaurant(
        id = restaurantEntity.restaurantId,
        name = restaurantEntity.name,
        description = restaurantEntity.description,
        image = restaurantEntity.image,
    )
    val branches = branches.map {
        Branch(
            id = it.branchId,
            longitude = it.longitude,
            latitude = it.latitude,
            address = it.address,
            restaurant = restaurant
        )
    }
    return restaurant.copy(branches = branches)
}