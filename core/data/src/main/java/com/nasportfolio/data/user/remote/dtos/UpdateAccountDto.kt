package com.nasportfolio.data.user.remote.dtos

data class UpdateAccountDto(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
    val image: ByteArray? = null,
    val fcmToken: String? = null,
    val deleteImage: Boolean? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UpdateAccountDto

        if (username != other.username) return false
        if (email != other.email) return false
        if (password != other.password) return false
        if (image != null) {
            if (other.image == null) return false
            if (!image.contentEquals(other.image)) return false
        } else if (other.image != null) return false
        if (fcmToken != other.fcmToken) return false
        if (deleteImage != other.deleteImage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username?.hashCode() ?: 0
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (password?.hashCode() ?: 0)
        result = 31 * result + (image?.contentHashCode() ?: 0)
        result = 31 * result + (fcmToken?.hashCode() ?: 0)
        result = 31 * result + (deleteImage?.hashCode() ?: 0)
        return result
    }
}