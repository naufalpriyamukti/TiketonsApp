package com.tiketons.app.modeldata

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class UserProfile(
    @SerialName("id") val id: String? = null, // UUID dari Auth
    @SerialName("email") val email: String,
    @SerialName("username") val username: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("role") val role: String = "USER" // "USER" atau "ADMIN"
)