package com.letyouknow.model

import java.io.Serializable

data class FindLCDDealGuestData(
    val dealID: String? = "",
    val userID: String? = "",
    val guestID: String? = "",
    val vehicleInventoryID: String? = "",
    var price: String? = "",
    val msrp: String? = "",
    val zipCode: String? = "",
    val searchRadius: String? = "",
    val loanType: String? = "",
    val initial: String? = "",
    val dealTimeStamp: String? = "",
    val promotionId: String? = "",
    val timeZoneOffset: String? = "",
    val ip: String? = "",
    val canSavePayment: String? = "",
    val payment_method_id: String? = "",
    val payment_intent_id: String? = "",
    val card_brand: String? = "",
    val card_last4: String? = "",
    var productId: String? = "",
    var yearId: String? = "",
    var makeId: String? = "",
    var modelId: String? = "",
    var trimId: String? = "",
    var exteriorColorId: String? = "",
    var interiorColorId: String? = "",
    var yearStr: String? = "",
    var makeStr: String? = "",
    var modelStr: String? = "",
    var trimStr: String? = "",
    var exteriorColorStr: String? = "",
    var interiorColorStr: String? = ""
) : Serializable