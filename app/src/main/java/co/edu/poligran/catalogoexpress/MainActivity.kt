package co.edu.poligran.catalogoexpress

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 * Pantalla principal con dos fragmentos en la misma pantalla:
 * a la izquierda el menú (MenuFragment) y a la derecha el contenido
 * de la opción seleccionada.
 */
class MainActivity : AppCompatActivity(), MenuFragment.OnOpcionSeleccionadaListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val usuario = Repositorio.usuarioActual
        if (usuario == null) {
            // Si no hay sesión activa (por ejemplo, la app se reinició) se vuelve al login.
            irAlLogin()
            return
        }

        supportActionBar?.subtitle = "Hola, ${usuario.nombre}"

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.contenedorMenu, MenuFragment(), ETIQUETA_MENU)
                .replace(R.id.contenedorContenido, CatalogoFragment())
                .commit()
        }
    }

    /** Evento que recibe la opción tocada en el fragmento del menú. */
    override fun onOpcionSeleccionada(opcion: OpcionMenu) {
        when (opcion) {
            OpcionMenu.PERFIL -> mostrarContenido(PerfilFragment())
            OpcionMenu.CATALOGO -> mostrarContenido(CatalogoFragment())
            OpcionMenu.CARRITO -> mostrarContenido(CarritoFragment())
            OpcionMenu.WEB -> mostrarContenido(WebFragment())
            OpcionMenu.SALIR -> confirmarCierreSesion()
        }
    }

    /** Reemplaza el fragmento de la derecha. */
    private fun mostrarContenido(fragmento: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedorContenido, fragmento)
            .commit()
    }

    /** Actualiza el número de productos que se ve sobre la opción Carrito. */
    fun actualizarContadorCarrito() {
        val menu = supportFragmentManager.findFragmentByTag(ETIQUETA_MENU) as? MenuFragment
        menu?.actualizarContador()
    }

    private fun confirmarCierreSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Seguro que quieres salir de tu cuenta?")
            .setPositiveButton("Salir") { _, _ ->
                Repositorio.cerrarSesion()
                irAlLogin()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun irAlLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    companion object {
        private const val ETIQUETA_MENU = "menu"
    }
}
