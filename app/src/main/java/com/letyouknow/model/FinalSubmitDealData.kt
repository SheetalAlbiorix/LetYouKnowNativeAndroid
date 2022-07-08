package com.letyouknow.model

import java.io.Serializable

data class FinalSubmitDealData(
    val foundMatch: Boolean? = false,
    val canDisplaySuccessResult: Boolean? = false,
    val isDisplayedPriceValid: Boolean? = false,
    val isBadRequest: Boolean? = false,
    val messageList: String? = "",
    val matchedDealerInfo: String? = "",
    val successResult: String? = "",
    val negativeResult: NegativeResult? = NegativeResult(),
    val paymentResponse: PaymentResponseData? = PaymentResponseData()
) : Serializable

data class NegativeResult(
    val firstLabel: String? = "",
    val secondLabel: String? = "",
    val lcdPrice: Float? = 0.0f,
    val dealID: String? = "0",
    val vehicleInventoryID: String? = "",
    val minimalDistance: String? = "",
    val dealPrice: Float? = 0.0f,
    val thirdLabel: String? = "",
    val ucdDeals: ArrayList<FindUcdDealData>? = ArrayList()
) : Serializable

data class PaymentResponseData(
    val requires_action: Boolean? = false,
    val payment_intent_client_secret: String? = "",
    val hasError: Boolean? = false,
    val errorMessage: String? = ""
) : Serializable