package com.letyouknow.model

import java.io.Serializable

data class PrefSearchDealData(
    var minutes: Int? = 0,
    var zipCode: String? = "",
    var isZipCode: Boolean? = false,
    var yearId: String? = "",
    var makeId: String? = "",
    var modelId: String? = "",
    var trimId: String? = "",
    var extColorId: String? = "",
    var intColorId: String? = "",
    var yearStr: String? = "",
    var makeStr: String? = "",
    var modelStr: String? = "",
    var trimStr: String? = "",
    var extColorStr: String? = "",
    var intColorStr: String? = "",
    var searchRadius: String? = "",
    var isUCDSel: Boolean? = false,
    var isUCDSelZipCode: Boolean? = false,
    var ucdPriceRangeID: String? = "",
    var lowerBorder: String? = "ANY PRICE",
    var upperBorder: String? = "",
) : Serializable