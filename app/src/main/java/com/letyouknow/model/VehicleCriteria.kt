package com.letyouknow.model

import java.io.Serializable

data class VehicleCriteria(
    val dealerAccessories: ArrayList<VehicleAccessoriesData>? = ArrayList(),
    val exteriorColor: String? = "",
    val interiorColor: String? = "",
    val make: String? = "",
    val model: String? = "",
    val packages: ArrayList<VehiclePackagesData>? = ArrayList(),
    val trim: String? = "",
    val year: String? = ""
) : Serializable