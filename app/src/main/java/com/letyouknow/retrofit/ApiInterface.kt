package com.letyouknow.retrofit

import com.letyouknow.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {

    //    @POST("auth/login")
//    fun login(@Body request: HashMap<String, String>): Call<LoginData>
    @POST("auth/loginmobile")
    fun login(@Body request: HashMap<String, String>): Call<LoginData>

    @POST("userprofile")
    fun signUp(@Body request: HashMap<String, String>): Call<SignupData>

    @POST("userprofile/forgotpassword")
    fun forgotPassword(@Body request: HashMap<String, String>): Call<Void>

    @GET("vehiclecriteria/getvehicleyears")
    fun getVehicleYears(
        @Query("productId") productId: String?,
        @Query("zipCode") zipCode: String?
    ): Call<ArrayList<VehicleYearData>>

    @GET("vehiclecriteria/getvehiclemakes")
    fun getVehicleMake(
        @Query("productId") productId: String?,
        @Query("yearId") yearId: String?,
        @Query("zipCode") zipCode: String?
    ): Call<ArrayList<VehicleMakeData>>

    @GET("vehiclecriteria/getvehiclemodels")
    fun getVehicleModels(
        @Query("productId") productId: String?,
        @Query("yearId") yearId: String?,
        @Query("makeId") makeId: String?,
        @Query("zipCode") zipCode: String?
    ): Call<ArrayList<VehicleModelData>>

    @GET("vehiclecriteria/getvehicletrims")
    fun getVehicleTrims(
        @Query("productId") productId: String?,
        @Query("yearId") yearId: String?,
        @Query("makeId") makeId: String?,
        @Query("modelId") modelId: String?,
        @Query("zipCode") zipCode: String?
    ): Call<ArrayList<VehicleTrimData>>

    @GET("vehiclecriteria/getvehicleexteriorcolors")
    fun getVehicleExteriorColors(
        @Query("productId") productId: String?,
        @Query("yearId") yearId: String?,
        @Query("makeId") makeId: String?,
        @Query("modelId") modelId: String?,
        @Query("trimId") trimId: String?,
        @Query("zipCode") zipCode: String?
    ): Call<ArrayList<ExteriorColorData>>

    @GET("vehiclecriteria/getvehicleinteriorcolors")
    fun getVehicleInteriorColors(
        @Query("productId") productId: String?,
        @Query("yearId") yearId: String?,
        @Query("makeId") makeId: String?,
        @Query("modelId") modelId: String?,
        @Query("trimId") trimId: String?,
        @Query("exteriorColorId") exteriorColorId: String?,
        @Query("zipCode") zipCode: String?
    ): Call<ArrayList<InteriorColorData>>


    @GET("map/IsValidZip")
    fun isValidZip(
        @Query("zip") zip: String?
    ): Call<Boolean>


    @GET("vehiclecriteria/getvehiclepackages")
    fun getVehiclePackages(
        @Query("productId") productId: String?,
        @Query("yearId") yearId: String?,
        @Query("makeId") makeId: String?,
        @Query("modelId") modelId: String?,
        @Query("trimId") trimId: String?,
        @Query("exteriorColorId") exteriorColorId: String?,
        @Query("interiorColorId") interiorColorId: String?,
        @Query("zipCode") zipCode: String?
    ): Call<ArrayList<VehiclePackagesData>>

    @POST("vehiclecriteria/getvehicledealeraccessories")
    fun getVehicleDealerAccessories(@Body request: HashMap<String, Any>): Call<ArrayList<VehicleAccessoriesData>>

    @POST("vehiclecriteria/checkVehiclePackagesInventory")
    fun checkVehiclePackagesInventory(@Body request: HashMap<String, Any>): Call<CheckedPackageData>

    @POST("vehiclecriteria/checkVehicleDealerAccessoriesInventory")
    fun checkVehicleAccessoriesInventory(@Body request: HashMap<String, Any>): Call<CheckedPackageData>

    @POST("findmydeal/findLCDDeal")
    fun findLCDDeal(@Body request: HashMap<String, Any>): Call<FindLCDDeaData>

    @POST("findmydeal/FindUCDDeal")
    fun findUCDDeal(@Body request: HashMap<String, Any>): Call<ArrayList<FindUcdDealData>>

    @POST("proceedwithdeal/submitpendingdealucd")
    fun submitPendingDealUcd(@Body request: HashMap<String, Any>): Call<SubmitPendingUcdData>

    @POST("proceedwithdeal/submitpendingdeallcd")
    fun submitPendingDealLCD(@Body request: HashMap<String, Any>): Call<SubmitPendingUcdData>

    @POST("proceedwithdeal/submitpendingdeal")
    fun submitPendingDeal(@Body request: HashMap<String, Any>): Call<SubmitPendingUcdData>

    @POST("submitmydeal/submitdeal/")
    fun submitdeal(@Body request: HashMap<String, Any>): Call<SubmitDealLCDData>

    @POST("auth/refresh")
    fun refresh(@Body request: HashMap<String, Any>): Call<RefreshTokenData>

    @POST("image/getimageid")
    fun getImageId(@Body request: HashMap<String, Any>): Call<String>

    @POST("image/getimageurl")
    fun getImageURL(@Body request: HashMap<String, Any>): Call<ArrayList<String>>

    @GET("promotion/validatepromocode")
    fun validatePromoCode(
        @Query("promoCode") promoCode: String?,
        @Query("dealId") dealId: String?
    ): Call<PromoCodeData>

    @GET("lykdollar/getlykdollar")
    fun getlykdollar(
        @Query("dealId") dealId: String?
    ): Call<String>

    @PUT("buyer")
    fun buyer(
        @Body request: HashMap<String, Any>
    ): Call<BuyerInfoData>


    @POST("submitmydeal/submitdeallcd")
    fun submitdeallcd(@Body request: HashMap<String, Any>): Call<SubmitDealLCDData>

    @POST("submitmydeal/submitdealucd")
    fun submitdealucd(@Body request: HashMap<String, Any>): Call<SubmitDealLCDData>

    @FormUrlEncoded
    @POST("https://api.stripe.com/v1/payment_methods")
    fun paymentMethods(
        @Field("type") type: String?,
        @Field("card[number]") cardNumbaer: String?,
        @Field("card[cvc]") cardcvc: String?,
        @Field("card[exp_month]") cardexp_month: String?,
        @Field("card[exp_year]") cardexp_year: String?,
        @Field("billing_details[address][postal_code]") billing_details: String?
    ): Call<CardStripeData>

    /* @Field("guid") guid: String?,
     @Field("muid") muid: String?,
     @Field("sid") sid: String?,
     @Field("time_on_page") time_on_page: String?,
     @Field("key") key: String?*/
//    @Headers("Authorization: Bearer pk_test_51HaDBECeSnBm0gpFvqOxWxW9jMO18C1lEIK5mcWf6ZWMN4w98xh8bPplgB8TOLdhutqGFUYtEHCVXh2nHWgnYTDw00Pe7zmGIA")

//    fun paymentMethods(@Body request: HashMap<String, Any>): Call<CardStripeData>

    @POST("test/getimageurls")
    fun interior360(@Body request: HashMap<String, Any>): Call<ArrayList<String>>

    @POST("msrp/GetMinMSRP")
    fun getMinMSRP(@Body request: HashMap<String, Any>): Call<Double>

    @POST("msrp/GetMSRPRange")
    fun getMSRPRange(@Body request: HashMap<String, Any>): Call<ArrayList<String>>

    @GET("userprofile")
    fun getUserProfile(): Call<UserProfileData>

    @GET("userprofile/savingstodate")
    fun savingsToDate(): Call<Float>

    @POST("userprofile/changepassword")
    fun changePassword(@Body request: ChangePasswordRequestData): Call<ResponseBody>

    @GET("userprofile/{getUserID}/notificationoptions")
    fun notificationOptions(
        @Path(value = "getUserID", encoded = true) planId: Int?
    ): Call<NotificationOptionsData>

    @PUT("userprofile/{getUserID}/NotificationOptions")
    fun notificationOptionsUpdate(
        @Path(value = "getUserID", encoded = true) planId: Int?,
        @Body request: HashMap<String, Any>
    ): Call<Boolean>

    @PUT("userprofile/{getUserID}")
    fun editUserProfile(
        @Path(value = "getUserID", encoded = true) planId: Int?,
        @Body request: HashMap<String, String>
    ): Call<EditUserProfileData>

    @GET("history/pricebid")
    fun priceBid(
    ): Call<ArrayList<BidPriceData>>

    @GET("history/transactions")
    fun transactionsHistory(
    ): Call<ArrayList<TransactionHistoryData>>

    //    https://lykbuyerwebapidemo.azurewebsites.net/api/history/transaction?code=VJ16014029
    @GET("history/transaction")
    fun transactionCode(
        @Query("code") code: String?
    ): Call<TransactionCodeData>

    @POST("vehiclecriteria/CheckVehicleStock")
    fun checkVehicleStock(@Body request: HashMap<String, Any>): Call<Boolean>

    @POST("vehiclecriteria/issold")
    fun isSold(@Body request: String): Call<Boolean>

    @GET("https://api.stripe.com/v1/payment_intents/{id}")
    fun paymentIntent(
        @Path(value = "id") id: String?,
        @Query("key") key: String?,
        @Query("is_stripe_sdk") is_stripe_sdk: Boolean,
        @Query("client_secret") client_secret: String
    ): Call<Boolean>

    @POST("ExternalAuth/Facebook")
    fun facebookLogin(@Body request: HashMap<String, Any>): Call<LoginData>

    @POST("ExternalAuth/google")
    fun googleLogin(@Body request: HashMap<String, Any>): Call<LoginData>

    @PUT("userprofile/socialmobile")
    fun socialMobile(@Body request: HashMap<String, Any>): Call<LoginData>

    @GET("referralprogram/current/balance")
    fun referralProgramCurrentBalance(): Call<Double>

    //    @HTTP(method = "get", path = "referralprogram/current", hasBody = true)
//    fun referralProgramCurrent(@Body request: HashMap<String, Any>): Call<CurrentReferralProgramData>
//  @HTTP(method = "get", path = "referralprogram/current", hasBody = true)
    @GET("referralprogram/current")
    fun referralProgramCurrent(): Call<CurrentReferralProgramData>

    @POST("referralprogram/current")
    fun referralProgramCurrentPost(@Body request: HashMap<String, Any>): Call<CurrentReferralProgramData>

    @POST("userprofile/devicepushtoken")
    fun devicePushToken(@Body request: HashMap<String, Any>): Call<DevicePushTokenData>

    @DELETE("userprofile/devicepushtoken/{token}")
    fun devicePushTokenDelete(@Path(value = "token") token: String?): Call<DevicePushTokenData>

    @GET("promotion/public")
    fun promotionPublic(): Call<PromotionData>

    @GET("activematching/deal/{dealId}")
    fun activeMatchingDeal(@Path(value = "dealId") token: String?): Call<ActiveMatchingData>

    @GET("tax/calculateTax")
    fun calculateTax(
        @Query("priceBid") priceBid: Double?,
        @Query("promocodeDiscount") promoCodeDiscount: Double?,
        @Query("lykDollars") lykDollars: Double?,
        @Query("abbrev") abbrev: String?
    ): Call<CalculateTaxData>

    @POST("rebate")
    fun rebate(@Body request: HashMap<String, Any>): Call<CalculateTaxData>

    @POST("rebate/reset")
    fun rebateReset(@Body request: HashMap<String, Any>): Call<CalculateTaxData>

    @POST("rebate/getlist")
    fun rebateGetList(@Body request: HashMap<String, Any>): Call<ArrayList<RebateListData>>

    @POST("rebate/CheckAvailability")
    fun checkRebate(@Body request: HashMap<String, Any>): Call<RebateCheckedData>

}
