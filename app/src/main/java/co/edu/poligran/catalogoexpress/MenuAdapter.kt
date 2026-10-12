package co.edu.poligran.catalogoexpress

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

/** Adaptador que dibuja cada opción del menú con su ícono y texto. */
class MenuAdapter(
    context: Context,
    private val opciones: List<OpcionMenu>
) : ArrayAdapter<OpcionMenu>(context, R.layout.item_menu, opciones) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val vista = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false)

        val ivIconoMenu: ImageView = vista.findViewById(R.id.ivIconoMenu)
        val tvTextoMenu: TextView = vista.findViewById(R.id.tvTextoMenu)
        val tvContadorMenu: TextView = vista.findViewById(R.id.tvContadorMenu)

        val opcion = opciones[position]
        ivIconoMenu.setImageResource(opcion.icono)
        tvTextoMenu.setText(opcion.texto)

        val cantidad = if (opcion == OpcionMenu.CARRITO) Repositorio.cantidadEnCarrito() else 0
        if (cantidad > 0) {
            tvContadorMenu.visibility = View.VISIBLE
            tvContadorMenu.text = if (cantidad > 9) "9+" else cantidad.toString()
        } else {
            tvContadorMenu.visibility = View.GONE
        }
        return vista
    }
}
