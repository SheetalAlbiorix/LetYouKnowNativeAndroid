package com.letyouknow.model

import java.io.Serializable

data class BidPriceData(
    val vehicleYear: String? = "",
    val vehicleMake: String? = "",
    val vehicleModel: String? = "",
    val vehicleTrim: String? = "",
    val vehicleExteriorColor: String? = "",
    val vehicleInteriorColor: String? = "",
    val vehiclePackages: VehiclePackagesData? = VehiclePackagesData(),
    val vehicleAccessories: VehicleAccessoriesData? = VehicleAccessoriesData(),
    val zipCode: String? = "",
    val label: String? = "",
    val price: String? = "",
    val transactionCode: String? = "",
    val timeStampFormatted: String? = "",
    val utcTimeStamp: String? = "",
    val miles: String? = "",
    val condition: String? = "",
    val vehicleInStockCheckInput: VehicleInStockCheckInput? = VehicleInStockCheckInput(),
    val isPackageNone: Boolean? = false,
    val searchRadius: String? = "",
) : Serializable

data class VehicleInStockCheckInput(
    val product: String? = "",
    val yearId: String? = "",
    val makeId: String? = "",
    val modelId: String? = "",
    val trimId: String? = "",
    val exteriorColorId: String? = "",
    val interiorColorId: String? = "",
    val zipcode: String? = "",
    val searchRadius: String? = "",
    val packageList: ArrayList<String>? = ArrayList(),
    val accessoryList: ArrayList<String>? = ArrayList(),
) : Serializable