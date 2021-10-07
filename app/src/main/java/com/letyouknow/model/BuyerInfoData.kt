package com.letyouknow.model

import java.io.Serializable

data class BuyerInfoData(
    val buyerId: String? = "",
    val firstName: String? = "",
    val middleName: String? = "",
    val lastName: String? = "",
    val phoneNumber: String? = "",
    val email: String? = "",
    val addressId: String? = "",
    val address1: String? = "",
    val address2: String? = "",
    val city: String? = "",
    val state: String? = "",
    val zipcode: String? = "",
    val country: String? = ""
) : Serializable