package com.example.stockpro

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sharedViewModel: StockViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {

        // PANTALLA 1: Login
        composable("login") {
            LoginScreen { nombre ->
                navController.navigate("catalog/$nombre")
            }
        }

        // PANTALLA 2: Catálogo
        composable(
            route = "catalog/{operario}",
            arguments = listOf(navArgument("operario") { type = NavType.StringType })
        ) { backStackEntry ->
            val operario = backStackEntry.arguments?.getString("operario") ?: "Desconocido"
            CatalogScreen(
                operario = operario,
                viewModel = sharedViewModel,
                onProductoClick = { id -> navController.navigate("edit/$id") },
                onVerReporteClick = { navController.navigate("report") }
            )
        }

        // PANTALLA 3: Edición de Stock
        composable(
            route = "edit/{productoId}",
            arguments = listOf(navArgument("productoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getInt("productoId") ?: 0
            EditStockScreen(
                productoId = productoId,
                viewModel = sharedViewModel,
                onGuardarVolver = { navController.popBackStack() }
            )
        }

        // PANTALLA 4: Reporte Financiero
        composable("report") {
            ReportScreen(viewModel = sharedViewModel) {
                navController.popBackStack()
            }
        }
    }
}
//Configuración de las rutas de navegación