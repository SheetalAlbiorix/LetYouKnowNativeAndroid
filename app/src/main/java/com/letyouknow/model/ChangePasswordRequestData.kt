package com.letyouknow.model

import java.io.Serializable

class ChangePasswordRequestData(
    var userName: String? = "",
    var currentPassword: String? = "",
    var newPassword: String? = ""
) : Serializable