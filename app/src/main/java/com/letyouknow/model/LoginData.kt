package com.letyouknow.model

import java.io.Serializable

data class LoginData(
    val buyerId: Int? = 0,
    val userName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val authToken: String = "",
    val refreshToken: String = ""
) : Serializable