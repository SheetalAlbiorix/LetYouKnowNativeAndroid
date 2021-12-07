package com.letyouknow.model

import java.io.Serializable

data class LoginData(
    val buyerId: Int? = 0,
    val userName: String = "",
    var password: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var authToken: String = "",
    var refreshToken: String = ""
) : Serializable