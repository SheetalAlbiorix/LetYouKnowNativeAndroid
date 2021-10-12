package com.letyouknow.retrofit

import com.letyouknow.model.*
import com.pionymessenger.model.SignupData
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @POST("auth/login")
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

    @FormUrlEncoded
    @POST("https://api.stripe.com/v1/payment_methods")
    fun paymentMethods(
        @Field("type") type: String?,
        @Field("card[number]") cardNumbaer: String?,
        @Field("card[cvc]") cardcvc: String?,
        @Field("card[exp_month]") cardexp_month: String?,
        @Field("card[exp_year]") cardexp_year: String?,
        @Field("billing_details[address][postal_code]") billing_details: String?,
        @Field("guid") guid: String?,
        @Field("muid") muid: String?,
        @Field("sid") sid: String?,
        @Field("time_on_page") time_on_page: String?,
        @Field("key") key: String?
    ): Call<CardStripeData>
//    @Headers("Authorization: Bearer pk_test_51HaDBECeSnBm0gpFvqOxWxW9jMO18C1lEIK5mcWf6ZWMN4w98xh8bPplgB8TOLdhutqGFUYtEHCVXh2nHWgnYTDw00Pe7zmGIA")

//    fun paymentMethods(@Body request: HashMap<String, Any>): Call<CardStripeData>

}