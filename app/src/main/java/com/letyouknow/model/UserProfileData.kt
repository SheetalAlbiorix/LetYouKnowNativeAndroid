package com.letyouknow.model

import java.io.Serializable

data class UserProfileData(
    val address1: String? = "",
    val address2: String = "",
    val addressId: String = "",
    val city: String? = "",
    val country: String? = "",
    val email: String? = "",
    val firstName: String? = "",
    val isActive: Boolean? = false,
    val isDeleted: Boolean? = false,
    val lastName: String? = "",
    val middleName: String? = "",
    val mobilePhone: String? = "",
    val notificationOptions: NotificationOptions,
    val notificationTypes: NotificationTypes,
    val password: String? = "",
    val phoneNumber: String? = "",
    val profileId: Int,
    val referrer: Any,
    val roles: List<String>,
    val state: String,
    val userName: String? = "",
    val zipcode: String
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