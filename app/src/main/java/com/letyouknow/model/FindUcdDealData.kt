package com.letyouknow.model

import java.io.Serializable

data class FindUCDMainData(
    var position: Int? = 0,
    var imageID: String? = "",
    var imgUrl: String? = "",
    var arUCD: ArrayList<FindUcdDealData?> = ArrayList()
) : Serializable

data class FindUcdDealData(
    val dealID: String? = "",
    val userID: String? = "",
    val guestID: String? = "",
    val vehicleInventoryID: String? = "",
    var price: Float? = 0.0f,
    val msrp: Float? = 0.0f,
    val zipCode: String? = "",
    val searchRadius: String? = "",
    val loanType: String? = "",
    val initial: String? = "",
    val dealTimeStamp: String? = "",
    var promotionId: String? = "",
    val timeZoneOffset: String? = "",
    val vehicleYear: String? = "",
    val vehicleMake: String? = "",
    val vehicleModel: String? = "",
    val vehicleTrim: String? = "",
    val vehicleExteriorColor: String? = "",
    val vehicleInteriorColor: String? = "",
    val yearId: String? = "",
    val makeId: String? = "",
    val modelId: String? = "",
    val trimId: String? = "",
    val vehiclePackages: ArrayList<VehiclePackagesData>? = ArrayList(),
    val vehicleAccessories: ArrayList<VehicleAccessoriesData>? = ArrayList(),
    val miles: String? = "",
    val condition: String? = "",
    val ip: String? = "",
    val canSavePayment: String? = "",
    val payment_method_id: String? = "",
    val payment_intent_id: String? = "",
    val card_brand: String? = "",
    val card_last4: String? = "",
    val imageId: String? = "",
    val exteriorColorId: String? = "",
    val interiorColorId: String? = "",
    var discount: Float? = 0.0f,
    var imageUrl: String? = "",
    var isSelect: Boolean? = false
) : Serializable