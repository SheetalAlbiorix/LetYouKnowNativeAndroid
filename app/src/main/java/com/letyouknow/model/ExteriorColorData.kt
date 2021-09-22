package com.letyouknow.model

import java.io.Serializable

data class ExteriorColorData(
    var vehicleExteriorColorID: String? = "",
    val mfgCode: String? = "",
    var exteriorColor: String? = "",
    val msrp: String? = "",
    val isInInventory: String? = "",
    val id: String? = "",
) : Serializable