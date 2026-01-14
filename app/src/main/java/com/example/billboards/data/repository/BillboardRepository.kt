package com.example.billboards.data.repository

import com.example.billboards.data.api.BillboardApi
import com.example.billboards.data.model.BillboardDto
import com.example.billboards.data.model.BillboardDetailsDto
import com.example.billboards.data.model.BillboardImageDto
import com.example.billboards.data.model.UploadImageRequestDto

class BillboardRepository(private val api: BillboardApi) {

    suspend fun getBillboards(): List<BillboardDto> = api.getBillboards()

    suspend fun getBillboardDetails(id: String): BillboardDetailsDto = api.getBillboardDetails(id)

    suspend fun getBillboardImages(id: String): List<BillboardImageDto> = api.getBillboardImages(id)

    suspend fun uploadImage(id: String, body: UploadImageRequestDto): BillboardImageDto =
        api.postBillboardImage(id, body)
}
