package com.letyouknow.model

import java.io.Serializable

data class RebateCheckedData(
    val autoCheckList: ArrayList<String>? = ArrayList(),
    val grayOutList: ArrayList<String>? = ArrayList(),
    val status: String? = ""
) : Serializable