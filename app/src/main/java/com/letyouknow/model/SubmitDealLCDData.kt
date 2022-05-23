package com.letyouknow.model

import java.io.Serializable

data class SubmitDealLCDData(
    val canDisplaySuccessResult: Boolean? = false,
    val foundMatch: Boolean? = false,
    val isBadRequest: Boolean? = false,
    val somethingWentWrong: Boolean? = false,
    val isDisplayedPriceValid: Boolean? = false,
    var miles: String? = "",
    var conditions: String? = "",
    val matchedDealerInfo: MatchedDealerInfo? = MatchedDealerInfo(),
    val messageList: ArrayList<String>? = ArrayList(),
    val negativeResult: NegativeResult? = NegativeResult(),
    val paymentResponse: PaymentResponse? = PaymentResponse(),
    val successResult: SuccessResult? = SuccessResult()
) : Serializable

data class AddressInfo(
    val address1: String? = "",
    val address2: String? = "",
    val addressId: Int? = 0,
    val city: String? = "",
    val country: String? = "",
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0,
    val state: String? = "",
    val zipcode: String? = ""
)

data class MatchedDealerInfo(
    val addressID: String? = "",
    val addressInfo: AddressInfo? = AddressInfo(),
    val dealerCode: String? = "",
    val dealerID: Int? = 0,
    val documentationFee: Double? = 0.0,
    val email: String? = "",
    val exportEmail: String? = "",
    val hasDMS: Boolean? = false,
    val name: String? = "",
    val phoneNumber: String? = ""
)

data class PaymentResponse(
    val errorMessage: String? = "",
    val hasError: Boolean? = false,
    val payment_intent_client_secret: String? = "",
    val requires_action: Boolean? = false
)

data class SuccessResult(
    val inventoryID: Int? = 0,
    val savings: Double? = 0.0,
    val transactionCode: String? = "",
    val transactionInfo: TransactionInfo? = TransactionInfo()
)

data class TransactionInfo(
    val buyerAddress1: String? = "",
    val buyerAddress2: String? = "",
    val buyerCity: String? = "",
    val buyerEmail: String? = "",
    val buyerName: String? = "",
    val buyerPhone: String? = "",
    val buyerState: String? = "",
    val buyerZipcode: String? = "",
    val dealExpireDate: String? = "",
    val dealExpireDateFormatted: Any? = "",
    val dealerDto: Any? = "",
    val discount: Double? = 0.0,
    val financingOptions: String? = "",
    val ip: String? = "",
    val isPackageNone: Boolean? = false,
    val lap: Double? = 0.0,
    val lykDollar: Float? = 0.0f,
    val msrp: Double? = 0.0,
    val paymentLast4: String? = "",
    val paymentMethod: String? = "",
    val product: String? = "",
    var remainingBalance: Float? = 0.0f,
    val reservationPrepayment: Double? = 0.0,
    val savings: Double? = 0.0,
    val stockAge: Int? = 0,
    val stockNumber: Any? = "",
    val timeStampFormatted: String? = "",
    val timestamp: String? = "",
    val transactionCode: String? = "",
    val transactionId: String? = "",
    val vehicleAccessories: ArrayList<VehicleAccessory>? = ArrayList(),
    val vehicleCondition: String? = "",
    val vehicleExteriorColor: String? = "",
    val vehicleInteriorColor: String? = "",
    val vehicleMake: String? = "",
    val vehicleMiles: String? = "",
    val vehicleModel: String? = "",
    val vehiclePackages: ArrayList<VehiclePackage>? = ArrayList(),
    var vehiclePrice: Float? = 0.0f,
    var vehiclePromoCode: Float? = 0.0f,
    val vehicleTrim: String? = "",
    val vehicleVIN: String? = "",
    val vehicleYear: String? = ""
)

data class VehicleAccessory(
    val accessory: String? = "",
    val dealerAccessoryID: Int? = 0,
    val id: Int? = 0,
    val isFromDMS: Boolean? = false,
    val isInInventory: Boolean? = false,
    val mfgCode: Any? = "",
    val msrp: Double? = 0.0
)

data class VehiclePackage(
    val id: Int? = 0,
    val isInInventory: Boolean? = false,
    val mfgCode: Any? = "",
    val msrp: Double? = 0.0,
    val `package`: String? = "",
    val vehiclePackageID: Int? = 0
)