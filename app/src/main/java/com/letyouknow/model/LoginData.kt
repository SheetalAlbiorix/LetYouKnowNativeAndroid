package com.letyouknow.model

import java.io.Serializable

data class LoginData(
    val buyerId: Int? = 0,
    val userName: String? = "",
    var password: String? = "",
    var firstName: String? = "",
    var lastName: String? = "",
    var email: String? = "",
    var phoneNo: String? = "",
    var authToken: String? = "",
    var refreshToken: String? = "",
    var message: String? = "",
    var isSocial: Boolean? = false
) : Serializable

data class LoginErrorData(
    val login_failure: ArrayList<String>? = ArrayList(),
    val DuplicateUserName: ArrayList<String>? = ArrayList()
) : Serializable