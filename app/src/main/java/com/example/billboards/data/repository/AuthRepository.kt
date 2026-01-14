package com.example.billboards.data.repository

import com.example.billboards.data.storage.TokenStore
import kotlin.random.Random

class AuthRepository(private val tokenStore: TokenStore) {

    suspend fun login(email: String, password: String): Result<Unit> {
        // Mock: acceptă orice
        val userId = "u_${Random.nextInt(10, 999)}"
        val token = "mock_token_${System.currentTimeMillis()}"
        tokenStore.setSession(token, userId)
        return Result.success(Unit)
    }

    suspend fun logout() {
        tokenStore.clear()
    }
}
