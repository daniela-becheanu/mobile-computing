package com.example.billboards.data.model

data class BillboardDetailsDto(
    val id: String,
    val name: String,
    val city: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val resolution: String?,
    val screenSizeInInches: Int?,
    val availableSlots: List<TimeSlotDto>
)