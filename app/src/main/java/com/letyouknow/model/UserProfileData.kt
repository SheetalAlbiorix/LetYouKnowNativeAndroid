package com.letyouknow.model

import java.io.Serializable

data class UserProfileData(
    var address1: String? = "-",
    var address2: String? = "-",
    val addressId: String? = "-",
    var city: String? = "-",
    val country: String? = "-",
    var email: String? = "-",
    var firstName: String? = "-",
    val isActive: Boolean? = false,
    val isDeleted: Boolean? = false,
    var lastName: String? = "-",
    var middleName: String? = "-",
    val mobilePhone: String? = "-",
    val notificationOptions: NotificationOptions? = NotificationOptions(),
    val notificationTypes: NotificationTypes? = NotificationTypes(),
    val password: String? = "-",
    var phoneNumber: String? = "-",
    val profileId: Int? = 0,
    val referrer: Any? = "",
    val roles: ArrayList<String> = ArrayList(),
    var state: String? = "-",
    val userName: String? = "-",
    var zipcode: String? = "-"
) : Serializable

data class NotificationTypes(
    val DealReservationConfirmations: Boolean? = false,
    val PromosAndDeals: Boolean? = false,
    val ReservationReminders: Boolean? = false
)

data class NotificationOptions(
    val Email: Boolean? = false,
    val PushNotification: Boolean? = false,
    val SMS: Boolean? = false
)