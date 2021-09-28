package com.letyouknow.retrofit

import com.letyouknow.model.*
import com.pionymessenger.model.SignupData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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
        @Query("yearId") yearId: String?
    ): Call<ArrayList<VehicleMakeData>>

    @GET("vehiclecriteria/getvehiclemodels")
    fun getVehicleModels(
        @Query("productId") productId: String?,
        @Query("yearId") yearId: String?,
        @Query("makeId") makeId: String?
    ): Call<ArrayList<VehicleModelData>>

    @GET("vehiclecriteria/getvehicletrims")
    fun getVehicleTrims(
        @Query("productId") productId: String?,
        @Query("yearId") yearId: String?,
        @Query("makeId") makeId: String?,
        @Query("modelId") modelId: String?
    ): Call<ArrayList<VehicleTrimData>>

    @GET("vehiclecriteria/getvehicleexteriorcolors")
    fun getVehicleExteriorColors(
        @Query("productId") productId: String?,
        @Query("yearId") yearId: String?,
        @Query("makeId") makeId: String?,
        @Query("modelId") modelId: String?,
        @Query("trimId") trimId: String?
    ): Call<ArrayList<ExteriorColorData>>

    @GET("vehiclecriteria/getvehicleinteriorcolors")
    fun getVehicleInteriorColors(
        @Query("productId") productId: String?,
        @Query("yearId") yearId: String?,
        @Query("makeId") makeId: String?,
        @Query("modelId") modelId: String?,
        @Query("trimId") trimId: String?,
        @Query("exteriorColorId") exteriorColorId: String?
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
        @Query("interiorColorId") interiorColorId: String?
    ): Call<ArrayList<VehiclePackagesData>>

    @POST("vehiclecriteria/getvehicledealeraccessories")
    fun getVehicleDealerAccessories(@Body request: HashMap<String, String>): Call<SignupData>

    @POST("vehiclecriteria/checkVehiclePackagesInventory")
    fun checkVehiclePackagesInventory(@Body request: HashMap<String, Any>): Call<SignupData>
}