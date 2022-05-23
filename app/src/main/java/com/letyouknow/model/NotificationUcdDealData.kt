package com.letyouknow.model

import java.io.Serializable

data class NotificationUcdDealData(
    val VehicleYear: String? = "",
    val VehicleMake: String? = "",
    val VehicleModel: String? = "",
    val VehicleTrim: String? = "",
    val Vehicle: String? = "",
    val PriceDiscount: Float? = 0.0f,
    val Distance: String? = "",
    val ZipCode: String? = "",
    val UserProfileIds: String? = "",
    val UCDCriteria: UCDCriteria? = UCDCriteria()
) : Serializable

class UCDCriteria(
    var UserID: String? = "",
    var VehicleYearID: String? = "",
    var VehicleMakeID: String? = "",
    var VehicleModelID: String? = "",
    var VehicleTrimID: String? = "",
    var VehicleExteriorColorID: String? = "",
    var VehicleInteriorColorID: String? = "",
    var ZipCode: String? = "",
    var SearchRadius: String? = "",
    var IP: String? = "",
    var TimeZoneOffset: String? = "",
) : Serializable