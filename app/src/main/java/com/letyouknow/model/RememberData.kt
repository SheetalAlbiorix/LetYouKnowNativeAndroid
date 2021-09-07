package com.pionymessenger.model

import java.io.Serializable

data class RememberData(
    var email: String? = "",
    var password: String? = "",
) : Serializable