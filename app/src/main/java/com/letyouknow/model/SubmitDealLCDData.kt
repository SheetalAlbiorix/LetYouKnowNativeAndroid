package com.letyouknow.model

import java.io.Serializable

data class SubmitDealLCDData(
    val canDisplaySuccessResult: Boolean,
    val foundMatch: Boolean,
    val isBadRequest: Boolean,
    val isDisplayedPriceValid: Boolean,
    val matchedDealerInfo: MatchedDealerInfo,
    val messageList: ArrayList<String>? = ArrayList(),
    val negativeResult: NegativeResult? = NegativeResult(),
    val paymentResponse: PaymentResponse,
    val successResult: SuccessResult
) : Serializable

data class AddressInfo(
    val address1: String,
    val address2: String,
    val addressId: Int,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val state: String,
    val zipcode: String
)

data class MatchedDealerInfo(
    val addressID: Int,
    val addressInfo: AddressInfo,
    val dealerCode: String,
    val dealerID: Int,
    val documentationFee: Double,
    val email: String,
    val exportEmail: String,
    val hasDMS: Boolean,
    val name: String,
    val phoneNumber: String
)

data class PaymentResponse(
    val errorMessage: Any,
    val hasError: Boolean,
    val payment_intent_client_secret: Any,
    val requires_action: Boolean
)

data class SuccessResult(
    val inventoryID: Int,
    val savings: Double,
    val transactionCode: String,
    val transactionInfo: TransactionInfo
)

data class TransactionInfo(
    val buyerAddress1: String,
    val buyerAddress2: String,
    val buyerCity: String,
    val buyerEmail: String,
    val buyerName: String,
    val buyerPhone: String,
    val buyerState: String,
    val buyerZipcode: String,
    val dealExpireDate: String,
    val dealExpireDateFormatted: Any,
    val dealerDto: Any,
    val discount: Double,
    val financingOptions: String,
    val ip: String,
    val isPackageNone: Boolean,
    val lap: Double,
    val lykDollar: Float? = 0.0f,
    val msrp: Double,
    val paymentLast4: String,
    val paymentMethod: String,
    val product: String,
    var remainingBalance: Float? = 0.0f,
    val reservationPrepayment: Double? = 0.0,
    val savings: Double,
    val stockAge: Int,
    val stockNumber: Any,
    val timeStampFormatted: String,
    val timestamp: String,
    val transactionCode: String,
    val transactionId: String? = "",
    val vehicleAccessories: List<VehicleAccessory>,
    val vehicleCondition: String,
    val vehicleExteriorColor: String,
    val vehicleInteriorColor: String,
    val vehicleMake: String,
    val vehicleMiles: String,
    val vehicleModel: String,
    val vehiclePackages: List<VehiclePackage>,
    var vehiclePrice: Float? = 0.0f,
    val vehicleTrim: String,
    val vehicleVIN: String,
    val vehicleYear: String
)

data class VehicleAccessory(
    val accessory: String,
    val dealerAccessoryID: Int,
    val id: Int,
    val isFromDMS: Boolean,
    val isInInventory: Boolean,
    val mfgCode: Any,
    val msrp: Double
)

data class VehiclePackage(
    val id: Int,
    val isInInventory: Boolean,
    val mfgCode: Any,
    val msrp: Double,
    val `package`: String,
    val vehiclePackageID: Int
)