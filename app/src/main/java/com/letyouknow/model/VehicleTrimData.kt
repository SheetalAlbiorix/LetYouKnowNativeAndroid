package com.letyouknow.model

import java.io.Serializable


data class VehicleTrimData(
    var vehicleTrimID: String? = "",
    var trim: String? = "",
    val msrp: String? = "",
    val isInInventory: String? = "",
    val id: String? = "",
) : Serializable