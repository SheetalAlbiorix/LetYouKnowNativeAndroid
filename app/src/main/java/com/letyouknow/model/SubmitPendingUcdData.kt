package com.letyouknow.model

import java.io.Serializable

data class SubmitPendingUcdData(
    val dealID: String? = "",
    val isBadRequest: Boolean? = false,
    val guestID: String? = "",
    val messageList: String? = "",
    val buyer: BuyerInfoData? = BuyerInfoData()
) : Serializable