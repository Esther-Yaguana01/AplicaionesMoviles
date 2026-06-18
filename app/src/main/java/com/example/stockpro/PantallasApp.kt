package com.example.stockpro

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ========================================================
// PANTALLA 1: INGRESO DE OPERARIO (2 PUNTOS)
// ========================================================
@Composable
fun LoginScreen(onIngresarClick: (String) -> Unit) {
    var nombreOperario by remember { mutableStateOf("") }
    val esBotonHabilitado = nombreOperario.trim().length >= 3

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bienvenido a StockPro", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = nombreOperario,
            onValueChange = { nombreOperario = it },
            label = { Text("Nombre del Operario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onIngresarClick(nombreOperario.trim()) },
            enabled = esBotonHabilitado,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar al Sistema")
        }
    }
}

// ========================================================
// PANTALLA 2: CATÁLOGO DE INVENTARIO (3 PUNTOS)
// ========================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    operario: String,
    viewModel: StockViewModel,
    onProductoClick: (Int) -> Unit,
    onVerReporteClick: () -> Unit
) {
    var mostrarSoloCritico by remember { mutableStateOf(false) }

    val listaFiltrada = if (mostrarSoloCritico) {
        viewModel.obtenerProductosEnRiesgo()
    } else {
        viewModel.productos
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Operario: $operario") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onVerReporteClick) {
                Icon(Icons.Default.Assessment, contentDescription = "Ver Reporte")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { mostrarSoloCritico = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!mostrarSoloCritico) MaterialTheme.colorScheme.primary else Color.Gray
                    ),
                    modifier = Modifier.weight(1f)
                ) { Text("Ver Todo") }

                Button(
                    onClick = { mostrarSoloCritico = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (mostrarSoloCritico) MaterialTheme.colorScheme.error else Color.Gray
                    ),
                    modifier = Modifier.weight(1f)
                ) { Text("Stock Crítico") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listaFiltrada) { producto ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onProductoClick(producto.id) },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                            Text("Precio unitario: \$${producto.precio}")

                            val colorTextoStock = if (producto.stockActual < 5) Color.Red else Color.Unspecified
                            Text(
                                text = "Stock actual: ${producto.stockActual}",
                                color = colorTextoStock,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

// ========================================================
// PANTALLA 3: EDICIÓN DE STOCK (2 PUNTOS)
// ========================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditStockScreen(productoId: Int, viewModel: StockViewModel, onGuardarVolver: () -> Unit) {
    val producto = viewModel.obtenerProducto(productoId)

    Scaffold(
        topBar = { TopAppBar(title = { Text("Editar Stock") }) }
    ) { paddingValues ->
        if (producto != null) {
            var stockTemp by remember { mutableStateOf(producto.stockActual) }

            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(producto.nombre, style = MaterialTheme.typography.headlineMedium)
                Text(producto.descripcion, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Stock: $stockTemp", style = MaterialTheme.typography.displaySmall)

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { if (stockTemp > 0) stockTemp-- },
                        enabled = stockTemp > 0
                    ) { Text("-1", style = MaterialTheme.typography.titleLarge) }

                    Button(onClick = { stockTemp++ }) {
                        Text("+1", style = MaterialTheme.typography.titleLarge)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        viewModel.actualizarStock(productoId, stockTemp)
                        onGuardarVolver()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar y Volver")
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: Producto no encontrado")
            }
        }
    }
}

// ========================================================
// PANTALLA 4: REPORTE FINANCIERO (3 PUNTOS)
// ========================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(viewModel: StockViewModel, onVolverClick: () -> Unit) {
    val capitalTotal = viewModel.calcularValorTotalInventario()
    val productosEnCero = viewModel.obtenerProductosEnCero()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Reporte Financiero") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Capital Invertido Total", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(String.format("$ %.2f", capitalTotal), style = MaterialTheme.typography.headlineLarge)
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Indicadores de Alerta", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Productos con stock en cero: $productosEnCero", style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = onVolverClick, modifier = Modifier.fillMaxWidth()) {
                Text("Volver al Catálogo")
            }
        }
    }
}