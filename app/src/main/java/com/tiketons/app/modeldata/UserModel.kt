package com.tiketons.app.modeldata

data class UserModel(
    val email: String,
    val token: String,
    val name: String = "User",
    val id: String = "", // ID User dari database
    val isLogin: Boolean = false
)