package com.letyouknow.model

import java.io.Serializable

data class TransactionCodeData(
    val dealExpireDate: String? = "",
    val dealExpireDateFormatted: String? = "",
    val timestamp: String? = "",
    val timeStampFormatted: String? = "",
    val transactionId: String? = "",
    val transactionCode: String? = "",
    val buyerName: String? = "",
    val buyerAddress1: String? = "",
    val buyerAddress2: String? = "",
    val buyerCity: String? = "",
    val buyerState: String? = "",
    val buyerZipcode: String? = "",
    var buyerPhone: String? = "",
    val buyerEmail: String? = "",
    val vehiclePrice: Float? = 0.0f,
    val reservationPrepayment: String? = "",
    val remainingBalance: Float? = 0.0f,
    val vehicleYear: String? = "",
    val vehicleMake: String? = "",
    val vehicleModel: String? = "",
    val vehicleTrim: String? = "",
    val vehicleVIN: String? = "",
    val vehicleMiles: String? = "",
    val vehicleCondition: String? = "",
    val vehicleAccessories: ArrayList<VehicleAccessoriesData>? = ArrayList(),
    val vehicleExteriorColor: String? = "",
    val vehicleInteriorColor: String? = "",
    val vehiclePackages: ArrayList<VehiclePackagesData>? = ArrayList(),
    val financingOptions: String? = "",
    val discount: String? = "",
    val lap: Int? = 0,
    val stockNumber: String? = "",
    val stockAge: Int? = 0,
    val msrp: Float? = 0.0f,
    val lykDollar: Float? = 0.0f,
    val ip: String? = "",
    val savings: Float? = 0.0f,
    val dealerDto: DealerDto,
    val paymentLast4: String? = "",
    val paymentMethod: String? = "",
    val product: String? = "",
    var packageStr: String? = "",
    var accessoriesStr: String? = "",
    val isPackageNone: Boolean? = false
) : Serializable

data class AddressInfoData(
    val addressId: String? = "",
    val address1: String? = "",
    var address2: String? = "",
    val city: String? = "",
    val state: String? = "",
    val zipcode: String? = "",
    val country: String? = "",
    val latitude: String? = "",
    val longitude: String? = ""
)

data class DealerDto(
    val dealerID: String? = "",
    val dealerCode: String? = "",
    val name: String? = "",
    val email: String? = "",
    val phoneNumber: String? = "",
    val documentationFee: String? = "",
    val addressID: String? = "",
    val hasDMS: Boolean? = false,
    val addressInfo: AddressInfoData? = AddressInfoData(),
    val exportEmail: String? = ""
)