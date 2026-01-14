package com.example.billboards.data.model

data class BillboardImageDto(
    val id: String,
    val userId: String,
    val imageUrl: String,
    val start: String,
    val end: String,
    val status: String
)