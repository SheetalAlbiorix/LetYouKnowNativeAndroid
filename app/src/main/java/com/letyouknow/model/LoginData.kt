package com.pionymessenger.model

import java.io.Serializable

data class LoginData(
    val data: LoginDetailData = LoginDetailData(),
    val ID: Int = 0,
    val caps: Caps = Caps(),
    val cap_key: String = "",
    val roles: ArrayList<String> = ArrayList(),
    val allcaps: Allcaps = Allcaps(),
    val filter: Any = Any(),
) : Serializable

data class LoginDetailData(
    val ID: String = "",
    val deleted: String = "",
    val display_name: String = "",
    val spam: String = "",
    val user_activation_key: String = "",
    val user_avatar: Any = Any(),
    val user_email: String = "",
    val user_login: String = "",
    val user_nicename: String = "",
    val user_pass: String = "",
    val user_registered: String = "",
    val user_status: String = "",
    val user_url: String = "",
) : Serializable

data class Caps(
    val subscriber: Boolean = false,
) : Serializable

data class Allcaps(
    val level_0: Boolean = false,
    val read: Boolean = false,
    val subscriber: Boolean = false,
) : Serializable

