package com.example.billboards.ui.screens.billboards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billboards.data.model.BillboardDto
import com.example.billboards.data.repository.BillboardRepository
import com.example.billboards.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BillboardsViewModel(
    private val repo: BillboardRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<BillboardDto>>>(UiState.Idle)
    val state: StateFlow<UiState<List<BillboardDto>>> = _state

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading

            runCatching { repo.getBillboards() }
                .onSuccess { list ->
                    // Filtrăm București / Bucharest
                    val filtered = list.filter { it.city.equals("Bucharest", ignoreCase = true) }
                    _state.value = UiState.Success(filtered)
                }
                .onFailure {
                    _state.value = UiState.Error(it.message ?: "Eroare necunoscută")
                }
        }
    }
}
