package co.edu.poligran.catalogoexpress

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat

/** Adaptador de la lista de productos del catálogo. */
class ProductoAdapter(
    private val contexto: Context,
    private var productos: List<Producto>
) : BaseAdapter() {

    /** Reemplaza la lista mostrada (por ejemplo, al buscar o filtrar). */
    fun actualizar(nuevos: List<Producto>) {
        productos = nuevos
        notifyDataSetChanged()
    }

    override fun getCount(): Int = productos.size

    override fun getItem(position: Int): Producto = productos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val vista = convertView
            ?: LayoutInflater.from(contexto).inflate(R.layout.item_producto, parent, false)

        val ivProducto: ImageView = vista.findViewById(R.id.ivProducto)
        val tvNombreProducto: TextView = vista.findViewById(R.id.tvNombreProducto)
        val tvPrecioProducto: TextView = vista.findViewById(R.id.tvPrecioProducto)
        val tvEstadoProducto: TextView = vista.findViewById(R.id.tvEstadoProducto)

        val producto = productos[position]
        ivProducto.mostrarImagenDe(producto)
        tvNombreProducto.text = producto.nombre
        tvPrecioProducto.text = formatoPrecio(producto.precio)

        // RF-16: disponibilidad del producto
        if (producto.disponible) {
            tvEstadoProducto.text = "Disponible (${producto.stock})"
            tvEstadoProducto.setTextColor(ContextCompat.getColor(contexto, R.color.verde))
        } else {
            tvEstadoProducto.text = "Agotado"
            tvEstadoProducto.setTextColor(ContextCompat.getColor(contexto, R.color.rojo))
        }
        return vista
    }
}
