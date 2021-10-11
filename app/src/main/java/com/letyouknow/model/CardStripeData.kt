package com.letyouknow.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CardStripeData : Serializable {
    var id: String? = ""
    val created: String? = ""

    @SerializedName("object")
    var objectName: String? = ""
    val customer: String? = ""
    val livemode: Boolean? = false
    var type: String? = ""
    var billing_details: BillingDetailsData? = BillingDetailsData()
    var card: CardDetData? = CardDetData()
}

data class BillingDetailsData(
    val email: String? = "",
    val name: String? = "",
    val phone: String? = "",
    val address: AddressDara? = AddressDara(),
) : Serializable

data class AddressDara(
    val city: String? = "",
    val country: String? = "",
    val line1: String? = "",
    val line2: String? = "",
    val postal_code: String? = "",
    val state: String? = ""
) : Serializable

data class CardDetData(
    val brand: String? = "",
    val checks: ChecksData? = ChecksData(),
    val country: String? = "",
    val exp_month: String? = "",
    val exp_year: String? = "",
    val funding: String? = "",
    val generated_from: String? = "",
    val last4: String? = "",
    val networks: NetworksData? = NetworksData(),
    val three_d_secure_usage: ThreeDSecureUsageData? = ThreeDSecureUsageData(),
    val wallet: String? = ""
) : Serializable

data class ChecksData(
    val address_line1_check: String? = "",
    val address_postal_code_check: String? = "",
    val cvc_check: String? = ""
) : Serializable

data class NetworksData(
    val available: ArrayList<String>? = ArrayList(),
    val preferred: String? = ""
) : Serializable

data class ThreeDSecureUsageData(
    val supported: Boolean? = false
) : Serializable