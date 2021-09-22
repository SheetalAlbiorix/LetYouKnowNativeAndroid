package com.letyouknow.model

import java.io.Serializable

data class InteriorColorData(
    var vehicleInteriorColorID: String? = "",
    val mfgCode: String? = "",
    var interiorColor: String? = "",
    val msrp: String? = "",
    val isInInventory: String? = "",
    val id: String? = "",
) : Serializable