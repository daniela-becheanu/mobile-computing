package com.example.billboards.data.model

data class BillboardDto(
    val id: String,
    val name: String,
    val city: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val thumbnailUrl: String?
)