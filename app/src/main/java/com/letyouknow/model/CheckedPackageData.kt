package com.letyouknow.model

import java.io.Serializable

data class CheckedPackageData(
    var autoCheckList: ArrayList<String> = ArrayList(),
    var grayOutList: ArrayList<String> = ArrayList(),
    var status: Int? = 0,
    var hasMatch: Boolean? = false,
) : Serializable