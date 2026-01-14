package com.example.billboards.data.model

data class UploadImageRequestDto(
    val userId: String,
    val imageBase64: String,
    val start: String,
    val end: String
)