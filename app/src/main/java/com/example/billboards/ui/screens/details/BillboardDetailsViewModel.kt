package com.example.billboards.ui.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billboards.data.model.BillboardDetailsDto
import com.example.billboards.data.model.BillboardImageDto
import com.example.billboards.data.model.UploadImageRequestDto
import com.example.billboards.data.repository.BillboardRepository
import com.example.billboards.data.storage.TokenStore
import com.example.billboards.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DetailsScreenModel(
    val details: BillboardDetailsDto? = null,
    val images: List<BillboardImageDto> = emptyList()
)

class BillboardDetailsViewModel(
    private val billboardId: String,
    private val repo: BillboardRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<DetailsScreenModel>>(UiState.Idle)
    val state: StateFlow<UiState<DetailsScreenModel>> = _state

    private val _uploadState = MutableStateFlow<UiState<BillboardImageDto>>(UiState.Idle)
    val uploadState: StateFlow<UiState<BillboardImageDto>> = _uploadState

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading

            runCatching {
                val details = repo.getBillboardDetails(billboardId)
                val images = repo.getBillboardImages(billboardId)
                DetailsScreenModel(details = details, images = images)
            }
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.message ?: "Eroare la încărcare") }
        }
    }

    fun upload(imageBase64: String, start: String, end: String) {
        viewModelScope.launch {
            _uploadState.value = UiState.Loading

            val userId = tokenStore.getUserId() ?: "anonymous"
            val body = UploadImageRequestDto(
                userId = userId,
                imageBase64 = imageBase64,
                start = start,
                end = end
            )

            runCatching { repo.uploadImage(billboardId, body) }
                .onSuccess { uploaded ->
                    _uploadState.value = UiState.Success(uploaded)
                    load() // refresh după upload
                }
                .onFailure { _uploadState.value = UiState.Error(it.message ?: "Upload eșuat") }
        }
    }
}
