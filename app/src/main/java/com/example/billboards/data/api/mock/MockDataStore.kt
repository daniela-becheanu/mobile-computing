package com.example.billboards.data.api.mock

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

object MockDataStore {

    private val billboards = listOf(
        JSONObject(
            mapOf(
                "id" to "bb_001",
                "name" to "Unirii Square - Billboard 1",
                "city" to "Bucharest",
                "address" to "Unirii Square",
                "lat" to 44.4268,
                "lng" to 26.1025,
                "thumbnailUrl" to "android.resource://com.example.billboards/drawable/bb_001_thumb"
            )
        ),
        JSONObject(
            mapOf(
                "id" to "bb_002",
                "name" to "Victory Square - Billboard 2",
                "city" to "Bucharest",
                "address" to "Victory Square",
                "lat" to 44.4527,
                "lng" to 26.0867,
                "thumbnailUrl" to "android.resource://com.example.billboards/drawable/bb_002_thumb"
            )
        )
    )

    private val imagesByBillboardId: MutableMap<String, MutableList<JSONObject>> = mutableMapOf(
        "bb_001" to mutableListOf(
            JSONObject(
                mapOf(
                    "id" to "img_100",
                    "userId" to "u_01",
                    "imageUrl" to "https://picsum.photos/600/400?10",
                    "start" to "2026-01-16T09:00:00+02:00",
                    "end" to "2026-01-16T09:30:00+02:00",
                    "status" to "scheduled"
                )
            )
        ),
        "bb_002" to mutableListOf()
    )

    fun getBillboardsJson(): String {
        val arr = JSONArray()
        billboards.forEach { arr.put(it) }
        return arr.toString()
    }

    fun getBillboardDetailsJson(id: String): String? {
        val base = billboards.firstOrNull { it.getString("id") == id } ?: return null

        val slots = when (id) {
            "bb_001" -> JSONArray(
                listOf(
                    JSONObject(
                        mapOf(
                            "start" to "2026-01-16T10:00:00+02:00",
                            "end" to "2026-01-16T10:30:00+02:00",
                            "priceRon" to 120.0
                        )
                    ),
                    JSONObject(
                        mapOf(
                            "start" to "2026-01-16T12:00:00+02:00",
                            "end" to "2026-01-16T12:30:00+02:00",
                            "priceRon" to 150.0
                        )
                    )
                )
            )

            "bb_002" -> JSONArray(
                listOf(
                    JSONObject(
                        mapOf(
                            "start" to "2026-01-16T11:00:00+02:00",
                            "end" to "2026-01-16T11:30:00+02:00",
                            "priceRon" to 110.0
                        )
                    ),
                    JSONObject(
                        mapOf(
                            "start" to "2026-01-16T13:00:00+02:00",
                            "end" to "2026-01-16T13:30:00+02:00",
                            "priceRon" to 140.0
                        )
                    )
                )
            )

            else -> JSONArray()
        }

        val details = JSONObject(base.toString()) // copy
        details.put("resolution", "1920x1080")
        details.put("screenSizeInInches", 96)
        details.put("availableSlots", slots)
        return details.toString()
    }

    fun getBillboardImagesJson(id: String): String? {
        if (billboards.none { it.getString("id") == id }) return null
        val list = imagesByBillboardId.getOrPut(id) { mutableListOf() }
        val arr = JSONArray()
        list.forEach { arr.put(it) }
        return arr.toString()
    }

    /**
     * Simulates POST /billboards/{id}/images
     * Expected request body:
     *  { userId, imageBase64, start, end }
     */
    fun postBillboardImageJson(id: String, requestBody: String): String? {
        if (billboards.none { it.getString("id") == id }) return null

        val req = runCatching { JSONObject(requestBody) }.getOrNull() ?: JSONObject()

        val userId = req.optString("userId", "anonymous")
        val start = req.optString("start", "")
        val end = req.optString("end", "")

        val created = JSONObject(
            mapOf(
                "id" to "img_${UUID.randomUUID().toString().take(8)}",
                "userId" to userId,
                "imageUrl" to "https://picsum.photos/600/400?${(100..999).random()}",
                "start" to start,
                "end" to end,
                "status" to "scheduled"
            )
        )

        imagesByBillboardId.getOrPut(id) { mutableListOf() }.add(0, created)
        return created.toString()
    }
}
