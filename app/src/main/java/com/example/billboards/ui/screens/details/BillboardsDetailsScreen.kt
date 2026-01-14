package com.example.billboards.ui.screens.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.billboards.ui.common.UiState

@Composable
fun BillboardDetailsScreen(
    vm: BillboardDetailsViewModel,
    onBack: () -> Unit,
    onStartUpload: (slotStart: String, slotEnd: String) -> Unit
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) { vm.load() }

    when (val s = state) {
        UiState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
                Text("Se încarcă detaliile panoului...")
            }
        }

        is UiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Eroare: ${s.message}")
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = onBack) {
                    Text("Înapoi")
                }
            }
        }

        is UiState.Success -> {
            val details = s.data.details

            if (details == null) {
                Column(Modifier.fillMaxSize().padding(24.dp)) {
                    Text("Nu există detalii pentru acest panou.")
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = onBack) { Text("Înapoi") }
                }
                return
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(details.name, style = MaterialTheme.typography.headlineSmall)
                            Spacer(Modifier.height(4.dp))
                            Text(details.address, style = MaterialTheme.typography.bodyMedium)

                            details.resolution?.let {
                                Spacer(Modifier.height(4.dp))
                                Text("Rezoluție: $it", style = MaterialTheme.typography.bodySmall)
                            }
                            details.screenSizeInInches?.let {
                                Spacer(Modifier.height(2.dp))
                                Text("Diagonală: ${it}\"", style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        OutlinedButton(onClick = onBack) {
                            Text("Înapoi")
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text("Sloturi disponibile", style = MaterialTheme.typography.titleMedium)
                }

                items(details.availableSlots) { slot ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = "${slot.start} → ${slot.end}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Preț: ${slot.priceRon} RON",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Button(onClick = { onStartUpload(slot.start, slot.end) }) {
                                Text("Alege poză")
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(8.dp))
                    Text("Campanii existente", style = MaterialTheme.typography.titleMedium)
                }

                items(s.data.images) { img ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            AsyncImage(
                                model = img.imageUrl,
                                contentDescription = "Imagine campanie",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("${img.start} → ${img.end}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(2.dp))
                            Text("Status: ${img.status}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                if (s.data.images.isEmpty()) {
                    item {
                        Text(
                            "Nu există campanii programate (sau API-ul mock întoarce listă goală).",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        else -> Unit
    }
}
