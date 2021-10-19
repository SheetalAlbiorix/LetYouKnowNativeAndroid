package com.letyouknow.model

import java.io.Serializable

data class UserProfileData(
    var address1: String? = "-",
    var address2: String = "-",
    val addressId: String = "-",
    var city: String? = "-",
    val country: String? = "-",
    var email: String? = "-",
    var firstName: String? = "-",
    val isActive: Boolean? = false,
    val isDeleted: Boolean? = false,
    var lastName: String? = "-",
    var middleName: String? = "-",
    val mobilePhone: String? = "-",
    val notificationOptions: NotificationOptions,
    val notificationTypes: NotificationTypes,
    val password: String? = "-",
    var phoneNumber: String? = "-",
    val profileId: Int,
    val referrer: Any,
    val roles: List<String>,
    var state: String? = "-",
    val userName: String? = "-",
    var zipcode: String? = "-"
) : Serializable

data class NotificationTypes(
    val DealReservationConfirmations: Boolean,
    val PromosAndDeals: Boolean,
    val ReservationReminders: Boolean
)

data class NotificationOptions(
    val Email: Boolean,
    val PushNotification: Boolean,
    val SMS: Boolean
)