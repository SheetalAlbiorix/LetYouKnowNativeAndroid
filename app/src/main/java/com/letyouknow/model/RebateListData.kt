package com.letyouknow.model

import java.io.Serializable

data class RebateListData(
    val rebateId: String? = "",
    val rebateName: String? = "",
    val rebatePrice: Float? = 0.0f,
    var isSelect: Boolean? = false,
    var isGray: Boolean? = false,
    var isOtherSelect: Boolean? = false,
) : Serializable