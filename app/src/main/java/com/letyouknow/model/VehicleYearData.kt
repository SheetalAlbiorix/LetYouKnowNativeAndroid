package com.letyouknow.model

import java.io.Serializable

data class VehicleYearData(
    var vehicleYearID: String? = "",
    var year: String? = "",
    val isInInventory: String? = "",
    val id: String? = "",
) : Serializable