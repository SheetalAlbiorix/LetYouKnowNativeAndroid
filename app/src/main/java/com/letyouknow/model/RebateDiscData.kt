package com.letyouknow.model

import androidx.annotation.OptIn
import java.io.Serializable

data class RebateDiscData(
    val rebateName: String? = "",
    var isSelect: Boolean? = false,
    @OptIn val isGray: Boolean? = false,
    @OptIn val isOtherSelect: Boolean? = false,
) : Serializable