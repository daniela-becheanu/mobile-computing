package com.example.billboards.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billboards.data.repository.AuthRepository
import com.example.billboards.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repo: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val state: StateFlow<UiState<Unit>> = _state

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading

            runCatching { repo.login(email, password).getOrThrow() }
                .onSuccess { _state.value = UiState.Success(Unit) }
                .onFailure { _state.value = UiState.Error(it.message ?: "Login eșuat") }
        }
    }

    fun resetState() {
        _state.value = UiState.Idle
    }
}
