package com.letyouknow.model

import java.io.Serializable

data class RefreshTokenData(
    val auth_token: String? = "",
    val refresh_token: String? = ""
) : Serializable
