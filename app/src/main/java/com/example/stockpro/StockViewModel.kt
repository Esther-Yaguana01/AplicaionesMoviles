package com.example.stockpro

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

// 1. MODELO EXIGIDO EN EL EXAMEN
data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    var stockActual: Int
)

// 2. VIEWMODEL COMO CEREBRO CENTRAL
class StockViewModel : ViewModel() {

    // Lista reactiva con 6 productos iniciales precargados
    val productos = mutableStateListOf(
        Producto(1, "Laptop Pro X", "Computadora para desarrollo de software", 1250.0, 8),
        Producto(2, "Mouse Inalámbrico", "Mouse ergonómico con conexión Bluetooth", 45.0, 3),
        Producto(3, "Monitor 4K 27\"", "Monitor profesional IPS para diseño", 350.0, 6),
        Producto(4, "Teclado Mecánico", "Teclado RGB con switches silenciosos", 85.0, 2),
        Producto(5, "Disco Duro SSD 1TB", "Memoria sólida de alta velocidad", 90.0, 0),
        Producto(6, "Auriculares Con Cancelación", "Audífonos premium para oficina", 199.9, 12)
    )

    fun obtenerProducto(id: Int): Producto? {
        return productos.find { it.id == id }
    }

    fun actualizarStock(id: Int, nuevaCantidad: Int) {
        val index = productos.indexOfFirst { it.id == id }
        if (index != -1 && nuevaCantidad >= 0) {
            val productoModificado = productos[index].copy(stockActual = nuevaCantidad)
            productos[index] = productoModificado
        }
    }

    fun calcularValorTotalInventario(): Double {
        return productos.sumOf { it.precio * it.stockActual }
    }

    fun obtenerProductosEnRiesgo(): List<Producto> {
        return productos.filter { it.stockActual < 5 }
    }

    fun obtenerProductosEnCero(): Int {
        return productos.count { it.stockActual == 0 }
    }
}

//Estado centralizado del stock de productos
//Sincronización final de datos completada