package com.example.billboards.data.api.mock

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer

class MockInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val method = request.method
        val path = request.url.encodedPath // ex: "/billboards/bb_001/images"

        // 1) GET /billboards
        if (method == "GET" && path == "/billboards") {
            return jsonResponse(request, 200, MockDataStore.getBillboardsJson())
        }

        // Split path: ["billboards", "{id}", "images"?]
        val segments = path.trim('/').split('/').filter { it.isNotBlank() }

        // We only handle /billboards/...
        if (segments.isEmpty() || segments[0] != "billboards") {
            return chain.proceed(request)
        }

        // /billboards/{id}
        if (segments.size == 2 && method == "GET") {
            val id = segments[1]
            val body = MockDataStore.getBillboardDetailsJson(id)
                ?: return jsonResponse(request, 404, errorJson("Billboard not found"))
            return jsonResponse(request, 200, body)
        }

        // /billboards/{id}/images
        if (segments.size == 3 && segments[2] == "images") {
            val id = segments[1]

            return when (method) {
                "GET" -> {
                    val body = MockDataStore.getBillboardImagesJson(id)
                        ?: return jsonResponse(request, 404, errorJson("Billboard not found"))
                    jsonResponse(request, 200, body)
                }

                "POST" -> {
                    val reqBodyStr = requestBodyToString(request)
                    val created = MockDataStore.postBillboardImageJson(id, reqBodyStr)
                        ?: return jsonResponse(request, 404, errorJson("Billboard not found"))
                    jsonResponse(request, 200, created)
                }

                else -> jsonResponse(request, 405, errorJson("Method not allowed"))
            }
        }

        // Dacă nu e unul dintre endpoint-urile noastre, lăsăm request-ul să meargă mai departe (către server real, dacă există)
        return chain.proceed(request)
    }

    private fun jsonResponse(request: okhttp3.Request, code: Int, json: String): Response {
        return Response.Builder()
            .request(request)
            .code(code)
            .message(if (code in 200..299) "OK" else "ERROR")
            .protocol(Protocol.HTTP_1_1)
            .body(json.toResponseBody("application/json".toMediaType()))
            .addHeader("Content-Type", "application/json")
            .build()
    }

    private fun errorJson(message: String): String =
        """{"error":"$message"}"""

    private fun requestBodyToString(request: okhttp3.Request): String {
        val body = request.body ?: return "{}"
        val buffer = Buffer()
        body.writeTo(buffer)
        return buffer.readUtf8()
    }
}
