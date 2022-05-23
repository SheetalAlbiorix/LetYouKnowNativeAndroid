package com.letyouknow.model

import java.io.Serializable

data class SignupData(
    val message: String = ""
) : Serializable


data class SignupDataError(
    val DuplicateEmail: ArrayList<String>? = ArrayList(),
    val DuplicateUserName: ArrayList<String>? = ArrayList(),
) : Serializable