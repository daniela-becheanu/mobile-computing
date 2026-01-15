package com.example.billboards.ui.screens.billboards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
fun BillboardsScreen(
    vm: BillboardsViewModel,
    onOpenDetails: (String) -> Unit
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
                Text("Se încarcă panourile...")
            }
        }

        is UiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Eroare: ${s.message}")
            }
        }

        is UiState.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Billboards (Bucharest)",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(Modifier.height(8.dp))
                }

                items(s.data) { bb ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onOpenDetails(bb.id) }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(bb.name, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(bb.address, style = MaterialTheme.typography.bodyMedium)

                            bb.thumbnailUrl?.let { url ->
                                Spacer(Modifier.height(12.dp))
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Thumbnail panou",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        else -> Unit
    }
}
