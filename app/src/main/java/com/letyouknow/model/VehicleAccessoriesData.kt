package com.letyouknow.model

import java.io.Serializable

data class VehicleAccessoriesData(
    var dealerAccessoryID: String? = "",
    val mfgCode: String? = "",
    var accessory: String? = "",
    val msrp: String? = "",
    val isFromDMS: Boolean? = false,
    val isInInventory: Boolean? = false,
    val id: String? = "",
    var isSelect: Boolean? = false,
    var isOtherSelect: Boolean? = false,
    var isGray: Boolean? = false
) : Serializable