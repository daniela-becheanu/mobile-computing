package com.example.billboards.data.api

import com.example.billboards.data.model.*
import retrofit2.http.*

interface BillboardApi {

    @GET("billboards")
    suspend fun getBillboards(): List<BillboardDto>

    @GET("billboards/{id}")
    suspend fun getBillboardDetails(@Path("id") id: String): BillboardDetailsDto

    @GET("billboards/{id}/images")
    suspend fun getBillboardImages(@Path("id") id: String): List<BillboardImageDto>

    @POST("billboards/{id}/images")
    suspend fun postBillboardImage(
        @Path("id") id: String,
        @Body body: UploadImageRequestDto
    ): BillboardImageDto
}
