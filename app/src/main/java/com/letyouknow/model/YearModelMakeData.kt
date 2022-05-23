package com.letyouknow.model

import java.io.Serializable

data class YearModelMakeData(
    var firstName: String? = "",
    var vehicleYearID: String? = "",
    var vehicleMakeID: String? = "",
    var vehicleModelID: String? = "",
    var vehicleTrimID: String? = "",
    var vehicleExtColorID: String? = "",
    var vehicleIntColorID: String? = "",
    var vehicleYearStr: String? = "",
    var vehicleMakeStr: String? = "",
    var vehicleModelStr: String? = "",
    var vehicleTrimStr: String? = "",
    var vehicleExtColorStr: String? = "",
    var vehicleIntColorStr: String? = "",
    var radius: String? = "",
    var zipCode: String? = "",
    var loanType: String? = "",
    var price: Float? = 0.0f,
    var msrp: Float? = 0.0f,
    var discount: Float? = 0.0f,
    var promotionId: String? = "",
    var initials: String? = "",
    var arPackages: ArrayList<VehiclePackagesData>? = ArrayList(),
    var arOptions: ArrayList<VehicleAccessoriesData>? = ArrayList()
) : Serializable