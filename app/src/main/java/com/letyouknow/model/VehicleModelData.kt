package com.letyouknow.model

import java.io.Serializable

data class VehicleModelData(
    val vehicleModelID: String? = "",
    var model: String? = "",
    val destinationFee: String? = "",
    val isInInventory: Boolean? = false,
    val id: Int? = 0,
) : Serializable