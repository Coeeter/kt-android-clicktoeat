package com.nasportfolio.data.branch.local

import androidx.room.*
import com.nasportfolio.data.restaurant.local.RestaurantEntity
import com.nasportfolio.domain.branch.Branch
import com.nasportfolio.domain.restaurant.Restaurant

@Entity(tableName = "branch")
data class BranchEntity(
    @PrimaryKey
    val branchId: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    @ColumnInfo(name = "restaurant_id")
    val restaurantId: String
)

data class BranchWithRestaurant(
    @Embedded
    val branchEntity: BranchEntity,
    @Relation(
        parentColumn = "restaurant_id",
        entityColumn = "restaurantId"
    )
    val restaurantEntity: RestaurantEntity?,
)

fun BranchWithRestaurant.toExternalBranch(): Branch? {
    restaurantEntity ?: return null
    return Branch(
        id = branchEntity.branchId,
        latitude = branchEntity.latitude,
        longitude = branchEntity.longitude,
        address = branchEntity.address,
        restaurant = Restaurant(
            id = restaurantEntity.restaurantId,
            name = restaurantEntity.name,
            description = restaurantEntity.description,
            image = restaurantEntity.image
        )
    )
}

fun Branch.toBranchEntity() = BranchEntity(
    branchId = id,
    address = address,
    longitude = longitude,
    latitude = latitude,
    restaurantId = restaurant.id
)