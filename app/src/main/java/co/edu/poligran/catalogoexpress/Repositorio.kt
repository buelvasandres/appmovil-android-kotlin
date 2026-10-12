package co.edu.poligran.catalogoexpress

/**
 * Fuente de datos de la aplicación.
 *
 * En esta versión los datos se guardan en memoria. En una mejora futura este
 * objeto se conectaría a una base de datos (por ejemplo Room o Firebase) sin
 * tener que cambiar las pantallas.
 */
object Repositorio {

    const val COSTO_ENVIO_EXPRESS = 10000
    const val TODAS = "Todas"

    private val usuarios = mutableListOf<Usuario>()
    private var consecutivoPedido = 1000

    var usuarioActual: Usuario? = null
        private set

    val productos = mutableListOf<Producto>()
    val carrito = mutableListOf<ItemCarrito>()
    val categorias = listOf(TODAS, "Tecnología", "Ropa", "Calzado", "Accesorios", "Hogar")

    init {
        cargarDatosDeEjemplo()
    }

    // ------------------------------------------------------------------
    // Usuarios (RF-01 a RF-06)
    // ------------------------------------------------------------------

    /** RF-02. Devuelve un mensaje de error o null si el acceso es correcto. */
    fun iniciarSesion(correo: String, contrasena: String): String? {
        val usuario = usuarios.find { it.correo.equals(correo.trim(), ignoreCase = true) }
            ?: return "No existe una cuenta con ese correo."
        if (usuario.contrasena != contrasena) {
            return "Contraseña incorrecta."
        }
        usuarioActual = usuario
        carrito.clear()
        return null
    }

    /** RF-01 y RF-04. Devuelve un mensaje de error o null si se registró. */
    fun registrar(nuevo: Usuario): String? {
        val existe = usuarios.any { it.correo.equals(nuevo.correo, ignoreCase = true) }
        if (existe) {
            return "Ya existe una cuenta registrada con ese correo."
        }
        usuarios.add(nuevo)
        return null
    }

    /** RF-03. */
    fun cerrarSesion() {
        usuarioActual = null
        carrito.clear()
    }

    // ------------------------------------------------------------------
    // Catálogo (RF-08, RF-13, RF-14)
    // ------------------------------------------------------------------

    fun buscarProductos(texto: String, categoria: String): List<Producto> {
        val busqueda = texto.trim().lowercase()
        return productos.filter { producto ->
            val coincideCategoria = categoria == TODAS || producto.categoria == categoria
            val coincideTexto = busqueda.isEmpty() ||
                producto.nombre.lowercase().contains(busqueda) ||
                producto.descripcion.lowercase().contains(busqueda) ||
                producto.codigo.lowercase().contains(busqueda)
            coincideCategoria && coincideTexto
        }
    }

    // ------------------------------------------------------------------
    // Carrito (RF-20 a RF-22)
    // ------------------------------------------------------------------

    /** RF-20. Devuelve un mensaje de error o null si se agregó. */
    fun agregarAlCarrito(producto: Producto): String? {
        if (!producto.disponible) {
            return "${producto.nombre} está agotado."
        }
        val item = carrito.find { it.producto.codigo == producto.codigo }
        val cantidadNueva = (item?.cantidad ?: 0) + 1
        if (cantidadNueva > producto.stock) {
            return "Solo hay ${producto.stock} unidades disponibles."
        }
        if (item == null) {
            carrito.add(ItemCarrito(producto, 1))
        } else {
            item.cantidad = cantidadNueva
        }
        return null
    }

    /** RF-21. Devuelve un mensaje de error o null. */
    fun cambiarCantidad(item: ItemCarrito, cantidadNueva: Int): String? {
        if (cantidadNueva < 1) {
            carrito.remove(item)
            return null
        }
        if (cantidadNueva > item.producto.stock) {
            return "Solo hay ${item.producto.stock} unidades disponibles."
        }
        item.cantidad = cantidadNueva
        return null
    }

    fun eliminarDelCarrito(item: ItemCarrito) {
        carrito.remove(item)
    }

    fun vaciarCarrito() {
        carrito.clear()
    }

    fun cantidadEnCarrito(): Int = carrito.sumOf { it.cantidad }

    fun subtotalCarrito(): Int = carrito.sumOf { it.subtotal }

    /** RF-22. Descuenta el inventario, vacía el carrito y devuelve el número de pedido. */
    fun confirmarCompra(): Int {
        for (item in carrito) {
            item.producto.stock -= item.cantidad
        }
        carrito.clear()
        consecutivoPedido++
        return consecutivoPedido
    }

    // ------------------------------------------------------------------
    // Datos de ejemplo
    // ------------------------------------------------------------------

    private fun cargarDatosDeEjemplo() {
        usuarios.add(
            Usuario(
                nombre = "Carlos Rodríguez",
                correo = "demo@catalogo.com",
                contrasena = "123456",
                telefono = "300 123 4567",
                ciudad = "Bogotá, Colombia",
                cargo = "Cliente frecuente",
                estudios = "• Ingeniería de Software – Politécnico Grancolombiano (en curso).\n\n" +
                    "• Tecnólogo en Análisis y Desarrollo de Software – SENA (2022).\n\n" +
                    "• Curso de Marketing Digital – Plataforma virtual (2024).\n\n" +
                    "• Inglés nivel B1 – Centro de idiomas (2021).",
                experiencia = "• Analista de soporte TI – Empresa de servicios (2023 – actualidad). " +
                    "Atención de usuarios, inventario de equipos y apoyo en compras tecnológicas.\n\n" +
                    "• Auxiliar de ventas – Tienda de tecnología (2021 – 2023). " +
                    "Asesoría a clientes, manejo de caja y organización del catálogo.\n\n" +
                    "• Practicante de desarrollo web – Agencia digital (2020). " +
                    "Mantenimiento de páginas y carga de productos en tienda en línea."
            )
        )

        productos.addAll(
            listOf(
                Producto(
                    "P001", "Audífonos inalámbricos",
                    "Audífonos con cancelación de ruido, batería de hasta 30 horas y conexión Bluetooth 5.3.",
                    85000, "Tecnología", 12, R.drawable.img_audifonos, true
                ),
                Producto(
                    "P002", "Camiseta negra",
                    "Camiseta básica de algodón 100 %, corte clásico. Tallas S a XL.",
                    105000, "Ropa", 20, R.drawable.img_camiseta, true
                ),
                Producto(
                    "P003", "Zapatillas blancas",
                    "Zapatillas urbanas de cuero sintético con suela antideslizante y plantilla acolchada.",
                    185000, "Calzado", 8, R.drawable.img_zapatillas, true
                ),
                Producto(
                    "P004", "Mouse para computador",
                    "Mouse inalámbrico con iluminación RGB, 6 botones y sensor de 7200 DPI.",
                    45000, "Tecnología", 15, R.drawable.img_mouse, true
                ),
                Producto(
                    "P005", "Teclado mecánico",
                    "Teclado mecánico compacto con switches rojos e iluminación RGB.",
                    159000, "Tecnología", 0, R.drawable.ic_keyboard, false
                ),
                Producto(
                    "P006", "Reloj inteligente",
                    "Reloj con monitor de ritmo cardiaco, contador de pasos y notificaciones del celular.",
                    249000, "Tecnología", 5, R.drawable.ic_watch, false
                ),
                Producto(
                    "P007", "Morral urbano",
                    "Morral con compartimento acolchado para portátil de hasta 15,6 pulgadas.",
                    120000, "Accesorios", 6, R.drawable.ic_backpack, false
                ),
                Producto(
                    "P008", "Lámpara LED de escritorio",
                    "Lámpara LED con tres tonos de luz y brazo flexible.",
                    68000, "Hogar", 10, R.drawable.ic_lightbulb, false
                )
            )
        )
    }
}
