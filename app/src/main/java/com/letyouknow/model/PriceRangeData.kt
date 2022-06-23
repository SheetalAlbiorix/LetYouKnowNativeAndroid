package com.letyouknow.model

import java.io.Serializable

data class PriceRangeData(
    var ucdPriceRangeID: String? = "",
    var lowerBorder: String? = "",
    val upperBorder: String? = "",
) : Serializable