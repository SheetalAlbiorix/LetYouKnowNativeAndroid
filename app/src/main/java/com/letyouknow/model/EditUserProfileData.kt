package com.letyouknow.model

data class EditUserProfileData(
    val authToken: String,
    val message: String,
    val refreshToken: String
)

/*
{"middleName":"",
"firstName":"Jackson","
lastName":"Badger",
"email":"jackson.badger112@gmail.com",
"confirmEmail":"jackson.badger112@gmail.com",
"userName":"jacksonbadger",
"phoneNumber":"2423452350",
"address1":"1120",
"address2":"Parkside Main St",
"city":"Cary",
"state":"NC",
"zipcode":"27519"}
*/