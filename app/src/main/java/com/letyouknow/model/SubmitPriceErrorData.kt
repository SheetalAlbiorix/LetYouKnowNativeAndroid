package com.letyouknow.model

import java.io.Serializable

data class SubmitPriceErrorData(
    val foundMatch: Boolean? = false,
    val canDisplaySuccessResult: Boolean? = false,
    val isDisplayedPriceValid: Boolean? = true,
    val isBadRequest: Boolean? = true,
    val messageList: ArrayList<String>? = ArrayList()
) : Serializable