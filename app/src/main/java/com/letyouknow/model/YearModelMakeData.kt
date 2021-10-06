package com.letyouknow.model

import java.io.Serializable

data class YearModelMakeData(
    var vehicleYearID: String? = "",
    var vehicleMakeID: String? = "",
    var vehicleModelID: String? = "",
    var vehicleTrimID: String? = "",
    var vehicleYearStr: String? = "",
    var vehicleMakeStr: String? = "",
    var vehicleModelStr: String? = "",
    var vehicleTrimStr: String? = ""
) : Serializable