package co.edu.poligran.catalogoexpress

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.fragment.app.Fragment

/** Fragmento izquierdo: lista de opciones del menú. */
class MenuFragment : Fragment(R.layout.fragment_menu) {

    /** La actividad implementa esta interfaz para saber qué opción se tocó. */
    interface OnOpcionSeleccionadaListener {
        fun onOpcionSeleccionada(opcion: OpcionMenu)
    }

    private var listener: OnOpcionSeleccionadaListener? = null

    private lateinit var lvMenu: ListView
    private lateinit var adaptador: MenuAdapter
    private val opciones = OpcionMenu.values().toList()
    private var posicionActual = OpcionMenu.CATALOGO.ordinal

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnOpcionSeleccionadaListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lvMenu = view.findViewById(R.id.lvMenu)
        adaptador = MenuAdapter(requireContext(), opciones)
        lvMenu.adapter = adaptador

        if (savedInstanceState == null) {
            lvMenu.setItemChecked(posicionActual, true)
        } else {
            posicionActual = savedInstanceState.getInt(CLAVE_POSICION, posicionActual)
        }

        // Evento: el usuario toca una opción del menú
        lvMenu.setOnItemClickListener { _, _, posicion, _ ->
            val opcion = opciones[posicion]
            if (opcion == OpcionMenu.SALIR) {
                // "Salir" no cambia el contenido; se mantiene marcada la opción anterior
                lvMenu.setItemChecked(posicionActual, true)
            } else {
                posicionActual = posicion
            }
            listener?.onOpcionSeleccionada(opcion)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CLAVE_POSICION, posicionActual)
    }

    /** Refresca el contador del carrito. */
    fun actualizarContador() {
        if (::adaptador.isInitialized) {
            adaptador.notifyDataSetChanged()
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        private const val CLAVE_POSICION = "posicion_menu"
    }
}
