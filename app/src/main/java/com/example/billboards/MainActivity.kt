package com.example.billboards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.billboards.data.api.NetworkModule
import com.example.billboards.data.repository.AuthRepository
import com.example.billboards.data.repository.BillboardRepository
import com.example.billboards.data.storage.TokenStore
import com.example.billboards.ui.screens.auth.AuthScreen
import com.example.billboards.ui.screens.auth.AuthViewModel
import com.example.billboards.ui.screens.billboards.BillboardsScreen
import com.example.billboards.ui.screens.billboards.BillboardsViewModel
import com.example.billboards.ui.screens.details.BillboardDetailsScreen
import com.example.billboards.ui.screens.details.BillboardDetailsViewModel
import com.example.billboards.ui.screens.upload.UploadScreen
import com.example.billboards.ui.theme.BillboardsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val baseUrl = "http://localhost/"

        val tokenStore = TokenStore(applicationContext)
        val api = NetworkModule.createApi(baseUrl = baseUrl, tokenStore = tokenStore)

        val billboardRepo = BillboardRepository(api)
        val authRepo = AuthRepository(tokenStore)

        setContent {
            BillboardsTheme {
                Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier) {
                    AppNavGraph(
                        billboardRepo = billboardRepo,
                        authRepo = authRepo,
                        tokenStore = tokenStore
                    )
                }
            }
        }
    }
}

@Composable
private fun AppNavGraph(
    billboardRepo: BillboardRepository,
    authRepo: AuthRepository,
    tokenStore: TokenStore
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "auth"
    ) {

        // ✅ AUTH
        composable("auth") {
            val vm: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(authRepo)
            )

            AuthScreen(
                vm = vm,
                onLoginSuccess = {
                    navController.navigate("billboards") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        // ✅ LISTĂ PANOURI
        composable("billboards") {
            val vm: BillboardsViewModel = viewModel(
                factory = BillboardsViewModelFactory(billboardRepo)
            )

            BillboardsScreen(
                vm = vm,
                onOpenDetails = { id ->
                    navController.navigate("details/$id")
                }
            )
        }

        // ✅ DETALII PANOU
        composable(
            route = "details/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable

            val vm: BillboardDetailsViewModel = viewModel(
                factory = BillboardDetailsViewModelFactory(
                    billboardId = id,
                    repo = billboardRepo,
                    tokenStore = tokenStore
                )
            )

            BillboardDetailsScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                onStartUpload = { slotStart, slotEnd ->
                    navController.navigate("upload/$id?start=$slotStart&end=$slotEnd")
                }
            )
        }

        // ✅ UPLOAD SCREEN
        composable(
            route = "upload/{id}?start={start}&end={end}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("start") { type = NavType.StringType; defaultValue = "" },
                navArgument("end") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            val start = backStackEntry.arguments?.getString("start") ?: ""
            val end = backStackEntry.arguments?.getString("end") ?: ""

            val vm: BillboardDetailsViewModel = viewModel(
                factory = BillboardDetailsViewModelFactory(
                    billboardId = id,
                    repo = billboardRepo,
                    tokenStore = tokenStore
                )
            )

            UploadScreen(
                vm = vm,
                slotStart = start,
                slotEnd = end,
                onDone = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

/* ---------------------------
   ViewModel Factories (simple)
   --------------------------- */

class AuthViewModelFactory(
    private val repo: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(repo) as T
    }
}

class BillboardsViewModelFactory(
    private val repo: BillboardRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BillboardsViewModel(repo) as T
    }
}

class BillboardDetailsViewModelFactory(
    private val billboardId: String,
    private val repo: BillboardRepository,
    private val tokenStore: TokenStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BillboardDetailsViewModel(
            billboardId = billboardId,
            repo = repo,
            tokenStore = tokenStore
        ) as T
    }
}
