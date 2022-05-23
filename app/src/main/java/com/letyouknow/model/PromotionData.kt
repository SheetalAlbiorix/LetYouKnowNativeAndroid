package com.letyouknow.model

data class PromotionData(
    val promotionCode: String? = "",
    val discount: Double? = 0.0,
    val endDate: String? = ""
)