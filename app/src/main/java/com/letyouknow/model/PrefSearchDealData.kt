package com.letyouknow.model

import java.io.Serializable

data class PrefSearchDealData(
    var minutes: Int? = 0,
    var zipCode: String? = "",
    var isZipCode: Boolean? = false,
    var yearId: String? = "0",
    var makeId: String? = "0",
    var modelId: String? = "0",
    var trimId: String? = "0",
    var extColorId: String? = "0",
    var intColorId: String? = "0",
    var yearStr: String? = "ANY",
    var makeStr: String? = "ANY",
    var modelStr: String? = "ANY",
    var trimStr: String? = "ANY",
    var extColorStr: String? = "ANY",
    var intColorStr: String? = "ANY",
    var searchRadius: String? = "",
    var isUCDSel: Boolean? = false,
    var isUCDSelZipCode: Boolean? = false,
    var ucdPriceRangeID: String? = "",
    var lowerBorder: String? = "ANY PRICE",
    var upperBorder: String? = "",
) : Serializable