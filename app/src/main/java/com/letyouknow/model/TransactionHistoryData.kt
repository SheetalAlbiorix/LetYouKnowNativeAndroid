package com.letyouknow.model

data class VehicleInStockCheckInputData(
    var product: Int? = 0,
    var yearId: Int? = 0,
    var makeId: Int? = 0,
    var modelId: Int? = 0,
    var trimId: Int? = 0,
    var exteriorColorId: Int? = 0,
    var interiorColorId: Int? = 0,
    var packageList: ArrayList<Int>? = ArrayList(),
    var accessoryList: ArrayList<Int>? = ArrayList(),
    var zipcode: String? = "",
    var searchRadius: String? = ""
)

data class TransactionHistoryData(
    var vehicleYear: String? = "",
    var vehicleMake: String? = "",
    var vehicleModel: String? = "",
    var vehicleTrim: String? = "",
    var vehicleExteriorColor: String? = "",
    var vehicleInteriorColor: String? = "",
    val vehiclePackages: ArrayList<VehiclePackagesData>? = ArrayList(),
    val vehicleAccessories: ArrayList<VehicleAccessoriesData>? = ArrayList(),
    var zipCode: String? = "",
    var label: String? = "",
    var price: Float? = 0.0f,
    var transactionCode: String? = "",
    var timeStampFormatted: String? = "",
    var utcTimeStamp: String? = "",
    var miles: String? = "",
    var condition: String? = "",
    var vehicleInStockCheckInput: VehicleInStockCheckInputData? = VehicleInStockCheckInputData(),
    var isPackageNone: Boolean? = false,
    var searchRadius: String? = ""
)