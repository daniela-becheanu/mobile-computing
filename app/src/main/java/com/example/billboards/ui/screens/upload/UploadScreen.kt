package com.example.billboards.ui.screens.upload

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.billboards.ui.common.UiState
import com.example.billboards.ui.screens.details.BillboardDetailsViewModel

@Composable
fun UploadScreen(
    vm: BillboardDetailsViewModel,
    slotStart: String,
    slotEnd: String,
    onDone: () -> Unit,
    onBack: () -> Unit
) {
    val uploadState by vm.uploadState.collectAsState()
    val context = LocalContext.current

    var base64Image by remember { mutableStateOf<String?>(null) }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            base64Image = uriToBase64(context, uri)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Upload imagine", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        Text("Slot selectat:")
        Text("$slotStart → $slotEnd")

        Spacer(Modifier.height(18.dp))

        OutlinedButton(onClick = { picker.launch("image/*") }) {
            Text(if (base64Image == null) "Alege imagine" else "Imagine aleasă ✅")
        }

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = {
                val img = base64Image ?: return@Button
                vm.upload(img, slotStart, slotEnd)
            },
            enabled = base64Image != null && uploadState !is UiState.Loading
        ) {
            Text("Trimite către panou")
        }

        Spacer(Modifier.height(12.dp))

        when (val s = uploadState) {
            UiState.Loading -> {
                CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
                Text("Se încarcă imaginea...")
            }

            is UiState.Error -> {
                Text("Eroare: ${s.message}")
            }

            is UiState.Success -> {
                Text("Programare realizată ✅")
                Spacer(Modifier.height(10.dp))
                Button(onClick = onDone) {
                    Text("Gata")
                }
            }

            else -> Unit
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(onClick = onBack) {
            Text("Înapoi")
        }
    }
}

private fun uriToBase64(context: Context, uri: Uri): String {
    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: ByteArray(0)
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}
