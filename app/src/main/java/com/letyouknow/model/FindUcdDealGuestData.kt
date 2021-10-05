package com.letyouknow.model

import java.io.Serializable

data class FindUcdDealGuestData(
    val dealID: String? = "",
    val userID: String? = "",
    val guestID: String? = "",
    val vehicleInventoryID: String? = "",
    val price: String? = "",
    val msrp: String? = "",
    val zipCode: String? = "",
    val searchRadius: String? = "",
    val loanType: String? = "",
    val initial: String? = "",
    val dealTimeStamp: String? = "",
    val promotionId: String? = "",
    val timeZoneOffset: String? = "",
    val vehicleYear: String? = "",
    val vehicleMake: String? = "",
    val vehicleModel: String? = "",
    val vehicleTrim: String? = "",
    val vehicleExteriorColor: String? = "",
    val vehicleInteriorColor: String? = "",
    val vehicleAccessories: ArrayList<VehicleAccessoriesData>? = ArrayList(),
    val miles: String? = "",
    val condition: String? = "",
    val ip: String? = "",
    val canSavePayment: String? = "",
    val payment_method_id: String? = "",
    val payment_intent_id: String? = "",
    val card_brand: String? = "",
    val card_last4: String? = "",
    val imageId: String = "",
    var isSelect: Boolean? = false
) : Serializable