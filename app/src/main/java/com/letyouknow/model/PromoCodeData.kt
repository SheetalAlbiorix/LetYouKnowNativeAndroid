package com.letyouknow.model

import java.io.Serializable

data class PromoCodeData(
    var promotionID: String? = "",
    val promotionCode: String? = "",
    var discount: Float? = 0.0f,
    val startDate: String? = "",
    val endDate: String? = "",
    val isUnlimited: Boolean? = false,
    val isValid: Boolean? = false
) : Serializable