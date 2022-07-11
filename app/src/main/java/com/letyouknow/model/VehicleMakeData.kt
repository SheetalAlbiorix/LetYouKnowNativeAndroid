package com.letyouknow.model

import java.io.Serializable

data class VehicleMakeData(
    var vehicleMakeID: String? = "0",
    var make: String? = "",
    val isInInventory: Boolean? = false,
    val id: Int? = 0,
) : Serializable