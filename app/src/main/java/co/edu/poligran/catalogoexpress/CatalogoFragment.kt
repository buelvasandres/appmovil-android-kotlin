package co.edu.poligran.catalogoexpress

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Fragmento Catálogo (requisito "Fotos"): lista de imágenes con barra de
 * desplazamiento; al seleccionar una se muestra su descripción.
 * Incluye búsqueda (RF-13), filtro por categoría (RF-14) y agregar al carrito (RF-20).
 */
class CatalogoFragment : Fragment(R.layout.fragment_catalogo) {

    // Variables vinculadas a las vistas del layout fragment_catalogo.xml
    private lateinit var etBuscar: EditText
    private lateinit var spCategorias: Spinner
    private lateinit var lvProductos: ListView
    private lateinit var tvCatalogoVacio: TextView
    private lateinit var panelDetalle: LinearLayout
    private lateinit var ivDetalle: ImageView
    private lateinit var tvDetalleNombre: TextView
    private lateinit var tvDetallePrecio: TextView
    private lateinit var tvDetalleEstado: TextView
    private lateinit var tvDetalleDescripcion: TextView
    private lateinit var btnAgregarCarrito: Button
    private lateinit var btnCerrarDetalle: ImageButton

    private lateinit var adaptador: ProductoAdapter
    private var productoSeleccionado: Producto? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vincularVistas(view)
        configurarLista()
        configurarCategorias()
        configurarBusqueda()
        configurarDetalle()
        filtrar()
    }

    private fun vincularVistas(vista: View) {
        etBuscar = vista.findViewById(R.id.etBuscar)
        spCategorias = vista.findViewById(R.id.spCategorias)
        lvProductos = vista.findViewById(R.id.lvProductos)
        tvCatalogoVacio = vista.findViewById(R.id.tvCatalogoVacio)
        panelDetalle = vista.findViewById(R.id.panelDetalle)
        ivDetalle = vista.findViewById(R.id.ivDetalle)
        tvDetalleNombre = vista.findViewById(R.id.tvDetalleNombre)
        tvDetallePrecio = vista.findViewById(R.id.tvDetallePrecio)
        tvDetalleEstado = vista.findViewById(R.id.tvDetalleEstado)
        tvDetalleDescripcion = vista.findViewById(R.id.tvDetalleDescripcion)
        btnAgregarCarrito = vista.findViewById(R.id.btnAgregarCarrito)
        btnCerrarDetalle = vista.findViewById(R.id.btnCerrarDetalle)
    }

    private fun configurarLista() {
        adaptador = ProductoAdapter(requireContext(), Repositorio.productos)
        lvProductos.adapter = adaptador

        // Evento: al seleccionar una imagen se muestra su descripción
        lvProductos.setOnItemClickListener { _, _, posicion, _ ->
            mostrarDetalle(adaptador.getItem(posicion))
        }
    }

    private fun configurarCategorias() {
        val adaptadorCategorias = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            Repositorio.categorias
        )
        adaptadorCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategorias.adapter = adaptadorCategorias

        // Evento: cambio de categoría
        spCategorias.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filtrar()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se requiere ninguna acción
            }
        }
    }

    private fun configurarBusqueda() {
        // Evento: cada vez que cambia el texto de búsqueda se filtra la lista
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrar()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun configurarDetalle() {
        btnCerrarDetalle.setOnClickListener {
            panelDetalle.visibility = View.GONE
            productoSeleccionado = null
        }

        btnAgregarCarrito.setOnClickListener {
            val producto = productoSeleccionado ?: return@setOnClickListener
            val error = Repositorio.agregarAlCarrito(producto)
            val mensaje = error ?: "${producto.nombre} agregado al carrito"
            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
            (activity as? MainActivity)?.actualizarContadorCarrito()
        }
    }

    /** Aplica la búsqueda y la categoría seleccionada. */
    private fun filtrar() {
        if (!::adaptador.isInitialized) return
        val categoria = spCategorias.selectedItem?.toString() ?: Repositorio.TODAS
        val resultado = Repositorio.buscarProductos(etBuscar.text.toString(), categoria)
        adaptador.actualizar(resultado)

        val vacio = resultado.isEmpty()
        lvProductos.visibility = if (vacio) View.GONE else View.VISIBLE
        tvCatalogoVacio.visibility = if (vacio) View.VISIBLE else View.GONE
    }

    /** RF-09: muestra la información detallada del producto. */
    private fun mostrarDetalle(producto: Producto) {
        productoSeleccionado = producto
        ivDetalle.mostrarImagenDe(producto)
        tvDetalleNombre.text = producto.nombre
        tvDetallePrecio.text = formatoPrecio(producto.precio)
        tvDetalleDescripcion.text = "${producto.descripcion}\n\nCategoría: ${producto.categoria} · Código: ${producto.codigo}"

        if (producto.disponible) {
            tvDetalleEstado.text = "Disponible: ${producto.stock} unidades"
            tvDetalleEstado.setTextColor(ContextCompat.getColor(requireContext(), R.color.verde))
            btnAgregarCarrito.isEnabled = true
            btnAgregarCarrito.text = getString(R.string.btn_agregar_carrito)
        } else {
            tvDetalleEstado.text = "Agotado"
            tvDetalleEstado.setTextColor(ContextCompat.getColor(requireContext(), R.color.rojo))
            btnAgregarCarrito.isEnabled = false
            btnAgregarCarrito.text = "Producto agotado"
        }
        panelDetalle.visibility = View.VISIBLE
    }
}
