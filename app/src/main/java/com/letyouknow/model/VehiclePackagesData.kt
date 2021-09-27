package com.letyouknow.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class VehiclePackagesData : Serializable {
    var vehiclePackageID: String? = ""
    val mfgCode: String? = ""

    @SerializedName("package")
    var packageName: String? = ""
    val msrp: String? = ""
    val id: Boolean? = false
    var isSelect: Boolean? = false
}