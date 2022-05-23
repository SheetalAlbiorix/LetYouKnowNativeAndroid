package com.letyouknow.model

import java.io.Serializable

data class CurrentReferralProgramData(
    val referralProgramID: String? = "",
    val referralRewardType: String? = "",
    val balance: Double? = 0.0,
    val totalEarned: Double? = 0.0,
    val timestamp: String? = "",
    val utcTimeStamp: String? = "",
    val timeZoneOffset: String? = "",
    val inviteCode: String? = "",
    val referralProgramTransactionHistoryDTO: ArrayList<ReferralProgramDTO>? = ArrayList(),
) : Serializable

data class ReferralProgramDTO(
    val type: String? = "",
    val value: Double? = 0.0,
    val referralProgramType: String? = "",
    var referralType: String? = "",
    val timeStampFormatted: String? = "",
    val userIdentifier: String? = "",
    var identifier: String? = "",
    val transactionCode: String? = "",
) : Serializable