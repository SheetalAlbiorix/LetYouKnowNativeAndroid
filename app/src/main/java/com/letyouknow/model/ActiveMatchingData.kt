package com.letyouknow.model

import java.io.Serializable

data class ActiveMatchingData(
    val deal: Deal? = Deal(),
    val vehicleCriteria: VehicleCriteria? = VehicleCriteria(),
    val vehicleInStockCheckInput: VehicleInStockCheckInput? = VehicleInStockCheckInput(),
) : Serializable