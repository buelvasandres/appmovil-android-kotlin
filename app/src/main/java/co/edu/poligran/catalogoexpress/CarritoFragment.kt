package co.edu.poligran.catalogoexpress

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment

/**
 * Fragmento Carrito (requisito "Botones"): usa Button, ImageButton,
 * SwitchCompat, RadioButton y CheckBox (RF-20, RF-21, RF-22).
 */
class CarritoFragment : Fragment(R.layout.fragment_carrito) {

    // Variables vinculadas a las vistas del layout fragment_carrito.xml
    private lateinit var contenedorItems: LinearLayout
    private lateinit var tvCarritoVacio: TextView
    private lateinit var panelPago: LinearLayout
    private lateinit var swExpress: SwitchCompat
    private lateinit var rgPago: RadioGroup
    private lateinit var cbTerminosCompra: CheckBox
    private lateinit var tvSubtotal: TextView
    private lateinit var tvEnvio: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnVaciar: Button
    private lateinit var btnFinalizar: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vincularVistas(view)
        configurarEventos()
        mostrarCarrito()
    }

    private fun vincularVistas(vista: View) {
        contenedorItems = vista.findViewById(R.id.contenedorItems)
        tvCarritoVacio = vista.findViewById(R.id.tvCarritoVacio)
        panelPago = vista.findViewById(R.id.panelPago)
        swExpress = vista.findViewById(R.id.swExpress)
        rgPago = vista.findViewById(R.id.rgPago)
        cbTerminosCompra = vista.findViewById(R.id.cbTerminosCompra)
        tvSubtotal = vista.findViewById(R.id.tvSubtotal)
        tvEnvio = vista.findViewById(R.id.tvEnvio)
        tvTotal = vista.findViewById(R.id.tvTotal)
        btnVaciar = vista.findViewById(R.id.btnVaciar)
        btnFinalizar = vista.findViewById(R.id.btnFinalizar)
    }

    private fun configurarEventos() {
        // Switch: activa o desactiva el envío express
        swExpress.setOnCheckedChangeListener { _, _ -> actualizarTotales() }

        // CheckBox: habilita el botón de finalizar compra
        cbTerminosCompra.setOnCheckedChangeListener { _, _ -> actualizarTotales() }

        // RadioGroup: método de pago seleccionado
        rgPago.setOnCheckedChangeListener { _, _ ->
            Toast.makeText(requireContext(), "Pago con: ${metodoPago()}", Toast.LENGTH_SHORT).show()
        }

        btnVaciar.setOnClickListener { confirmarVaciado() }
        btnFinalizar.setOnClickListener { mostrarDialogoDireccion() }
    }

    /** Dibuja una fila por cada producto del carrito. */
    private fun mostrarCarrito() {
        contenedorItems.removeAllViews()

        for (item in Repositorio.carrito) {
            val fila = layoutInflater.inflate(R.layout.item_carrito, contenedorItems, false)

            val ivItemCarrito: ImageView = fila.findViewById(R.id.ivItemCarrito)
            val tvNombreItem: TextView = fila.findViewById(R.id.tvNombreItem)
            val tvSubtotalItem: TextView = fila.findViewById(R.id.tvSubtotalItem)
            val tvCantidad: TextView = fila.findViewById(R.id.tvCantidad)
            val btnMenos: ImageButton = fila.findViewById(R.id.btnMenos)
            val btnMas: ImageButton = fila.findViewById(R.id.btnMas)
            val btnEliminar: ImageButton = fila.findViewById(R.id.btnEliminar)

            ivItemCarrito.mostrarImagenDe(item.producto)
            tvNombreItem.text = item.producto.nombre
            tvSubtotalItem.text = "${formatoPrecio(item.producto.precio)} x ${item.cantidad} = ${formatoPrecio(item.subtotal)}"
            tvCantidad.text = item.cantidad.toString()

            // ImageButton: disminuir, aumentar y eliminar
            btnMenos.setOnClickListener { cambiarCantidad(item, item.cantidad - 1) }
            btnMas.setOnClickListener { cambiarCantidad(item, item.cantidad + 1) }
            btnEliminar.setOnClickListener {
                Repositorio.eliminarDelCarrito(item)
                Toast.makeText(requireContext(), "${item.producto.nombre} eliminado", Toast.LENGTH_SHORT).show()
                mostrarCarrito()
            }

            contenedorItems.addView(fila)
        }

        val vacio = Repositorio.carrito.isEmpty()
        tvCarritoVacio.visibility = if (vacio) View.VISIBLE else View.GONE
        panelPago.visibility = if (vacio) View.GONE else View.VISIBLE
        actualizarTotales()
        (activity as? MainActivity)?.actualizarContadorCarrito()
    }

    private fun cambiarCantidad(item: ItemCarrito, cantidadNueva: Int) {
        val error = Repositorio.cambiarCantidad(item, cantidadNueva)
        if (error != null) {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
        mostrarCarrito()
    }

    private fun costoEnvio(): Int = if (swExpress.isChecked) Repositorio.COSTO_ENVIO_EXPRESS else 0

    private fun totalAPagar(): Int = Repositorio.subtotalCarrito() + costoEnvio()

    private fun actualizarTotales() {
        tvSubtotal.text = "Subtotal: ${formatoPrecio(Repositorio.subtotalCarrito())}"
        tvEnvio.text = "Envío: ${formatoPrecio(costoEnvio())}"
        tvTotal.text = "Total: ${formatoPrecio(totalAPagar())}"
        btnFinalizar.isEnabled = cbTerminosCompra.isChecked && Repositorio.carrito.isNotEmpty()
    }

    private fun metodoPago(): String = when (rgPago.checkedRadioButtonId) {
        R.id.rbPse -> getString(R.string.pago_pse)
        R.id.rbContraentrega -> getString(R.string.pago_contraentrega)
        else -> getString(R.string.pago_tarjeta)
    }

    private fun confirmarVaciado() {
        AlertDialog.Builder(requireContext())
            .setTitle("Vaciar carrito")
            .setMessage("¿Quieres quitar todos los productos del carrito?")
            .setPositiveButton("Vaciar") { _, _ ->
                Repositorio.vaciarCarrito()
                mostrarCarrito()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /** RF-22: pide la dirección de envío y confirma la compra. */
    private fun mostrarDialogoDireccion() {
        val vista = layoutInflater.inflate(R.layout.dialog_direccion, null)
        val etDireccion: EditText = vista.findViewById(R.id.etDireccion)

        val dialogo = AlertDialog.Builder(requireContext())
            .setTitle(R.string.btn_finalizar)
            .setView(vista)
            .setPositiveButton("Confirmar", null)
            .setNegativeButton("Cancelar", null)
            .show()

        // Se valida la dirección antes de cerrar el diálogo
        dialogo.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val direccion = etDireccion.text.toString().trim()
            if (direccion.isEmpty()) {
                etDireccion.error = "Escribe la dirección de envío"
                return@setOnClickListener
            }
            dialogo.dismiss()
            confirmarCompra(direccion)
        }
    }

    private fun confirmarCompra(direccion: String) {
        val total = totalAPagar()
        val pago = metodoPago()
        val numeroPedido = Repositorio.confirmarCompra()

        AlertDialog.Builder(requireContext())
            .setTitle("¡Compra confirmada!")
            .setMessage(
                "Pedido #$numeroPedido\n" +
                    "Total: ${formatoPrecio(total)}\n" +
                    "Pago: $pago\n" +
                    "Envío a: $direccion"
            )
            .setPositiveButton("Aceptar", null)
            .show()

        swExpress.isChecked = false
        cbTerminosCompra.isChecked = false
        mostrarCarrito()
    }
}
