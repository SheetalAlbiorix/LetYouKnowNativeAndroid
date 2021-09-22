package com.letyouknow.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class VehiclePackagesData : Serializable {
    val vehiclePackageID: String? = ""
    val mfgCode: String? = ""

    @SerializedName("package")
    val packageName: String? = ""
    val msrp: String? = ""
    val id: Boolean? = false
}