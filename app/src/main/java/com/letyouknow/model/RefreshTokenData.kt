package com.letyouknow.model

import java.io.Serializable

data class RefreshTokenData(
    val AuthToken: String? = "",
    val RefreshToken: String? = ""
) : Serializable
