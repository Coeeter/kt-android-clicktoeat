package com.nasportfolio.data.user.remote.dtos

data class SignUpDto(
    val username: String,
    val email: String,
    val password: String,
    val fcmToken: String,
    val image: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SignUpDto

        if (username != other.username) return false
        if (email != other.email) return false
        if (password != other.password) return false
        if (fcmToken != other.fcmToken) return false
        if (image != null) {
            if (other.image == null) return false
            if (!image.contentEquals(other.image)) return false
        } else if (other.image != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + fcmToken.hashCode()
        result = 31 * result + (image?.contentHashCode() ?: 0)
        return result
    }
}
