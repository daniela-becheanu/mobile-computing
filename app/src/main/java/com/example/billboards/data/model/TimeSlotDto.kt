package com.example.billboards.data.model

data class TimeSlotDto(
    val start: String, // ISO-8601
    val end: String,   // ISO-8601
    val priceRon: Double
)