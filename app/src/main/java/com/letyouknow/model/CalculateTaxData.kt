package com.letyouknow.model

import java.io.Serializable

data class CalculateTaxData(
    val carSalesTax: Double? = 0.0,
    val nonTaxRegFee: Double? = 0.0,
    val estimatedTotalPrice: Double? = 0.0,
) : Serializable