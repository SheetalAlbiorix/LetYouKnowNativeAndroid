package com.letyouknow.model

import java.io.Serializable

data class RememberMeData(
    var email: String? = "",
    var password: String? = "",
    var isChecked: Boolean? = false
) : Serializable