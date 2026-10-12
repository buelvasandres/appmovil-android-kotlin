package co.edu.poligran.catalogoexpress

import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat

/** Usuario registrado en la aplicación. */
data class Usuario(
    var nombre: String,
    var correo: String,
    var contrasena: String,
    var telefono: String = "",
    var ciudad: String = "",
    var cargo: String = "Cliente",
    var estudios: String = "",
    var experiencia: String = ""
)

/** Producto del catálogo. */
data class Producto(
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val categoria: String,
    var stock: Int,
    @DrawableRes val imagen: Int,
    val esFotografia: Boolean
) {
    /** RF-16: el producto está disponible si hay unidades en inventario. */
    val disponible: Boolean
        get() = stock > 0
}

/** Producto agregado al carrito con su cantidad. */
data class ItemCarrito(
    val producto: Producto,
    var cantidad: Int
) {
    val subtotal: Int
        get() = producto.precio * cantidad
}

/** Opciones del menú izquierdo. */
enum class OpcionMenu(@StringRes val texto: Int, @DrawableRes val icono: Int) {
    PERFIL(R.string.menu_perfil, R.drawable.ic_person),
    CATALOGO(R.string.menu_catalogo, R.drawable.ic_photo_library),
    CARRITO(R.string.menu_carrito, R.drawable.ic_shopping_cart),
    WEB(R.string.menu_web, R.drawable.ic_language),
    SALIR(R.string.menu_salir, R.drawable.ic_logout)
}

/** Da formato de pesos colombianos: 185000 -> "$ 185.000". */
fun formatoPrecio(valor: Int): String {
    val texto = valor.toString()
    val resultado = StringBuilder()
    for (i in texto.indices) {
        if (i > 0 && (texto.length - i) % 3 == 0) {
            resultado.append('.')
        }
        resultado.append(texto[i])
    }
    return "\$ $resultado"
}

/**
 * Muestra la imagen de un producto. Las fotografías se ven con sus colores
 * originales y los íconos se pintan con el morado de la marca.
 */
fun ImageView.mostrarImagenDe(producto: Producto) {
    setImageResource(producto.imagen)
    if (producto.esFotografia) {
        ImageViewCompat.setImageTintList(this, null)
    } else {
        val morado = ContextCompat.getColor(context, R.color.morado_oscuro)
        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(morado))
    }
}
