package com.nasportfolio.data.restaurant.remote.dtos

data class CreateRestaurantDto(
    val name: String,
    val description: String,
    val image: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreateRestaurantDto

        if (name != other.name) return false
        if (description != other.description) return false
        if (image != null) {
            if (other.image == null) return false
            if (!image.contentEquals(other.image)) return false
        } else if (other.image != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + (image?.contentHashCode() ?: 0)
        return result
    }
}