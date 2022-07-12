package com.letyouknow.model

import java.io.Serializable

data class CalculateTaxData(
    var estimatedRebates: Double? = 0.0,
    val rebateDetails: String? = "",
    val carSalesTax: Double? = 0.0,
    val nonTaxRegFee: Double? = 0.0,
    val estimatedTotalPrice: Double? = 0.0,
) : Serializable