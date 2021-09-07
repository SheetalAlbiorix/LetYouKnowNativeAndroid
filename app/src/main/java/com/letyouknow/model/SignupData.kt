package com.pionymessenger.model

import java.io.Serializable

data class SignupData(
    val ID: Int = 0,
    val display_name: String = "",
    val first_name: String = "",
    val last_name: String = "",
    val user_email: String = "",
    val user_login: String = "",
    val user_nicename: String = "",
) : Serializable