package com.letyouknow.model

import java.io.Serializable

data class MarketConditionsData(
    val BidPrice: Float? = 0.0f,
    val UserProfileId: String? = "",
    val DealId: String? = ""
) : Serializable