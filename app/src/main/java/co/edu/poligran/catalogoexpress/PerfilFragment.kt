package co.edu.poligran.catalogoexpress

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

/** Fragmento Perfil (RF-05, RF-06): foto, datos, estudios y experiencia. */
class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    // Variables vinculadas a las vistas del layout fragment_perfil.xml
    private lateinit var tvNombrePerfil: TextView
    private lateinit var tvCargoPerfil: TextView
    private lateinit var tvCorreoPerfil: TextView
    private lateinit var tvTelefonoPerfil: TextView
    private lateinit var tvCiudadPerfil: TextView
    private lateinit var tvEstudios: TextView
    private lateinit var tvExperiencia: TextView
    private lateinit var btnEditarPerfil: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vincularVistas(view)

        // Barra de desplazamiento propia para los textos largos
        tvEstudios.movementMethod = ScrollingMovementMethod()
        tvExperiencia.movementMethod = ScrollingMovementMethod()

        btnEditarPerfil.setOnClickListener { mostrarDialogoEdicion() }
        mostrarDatos()
    }

    private fun vincularVistas(vista: View) {
        tvNombrePerfil = vista.findViewById(R.id.tvNombrePerfil)
        tvCargoPerfil = vista.findViewById(R.id.tvCargoPerfil)
        tvCorreoPerfil = vista.findViewById(R.id.tvCorreoPerfil)
        tvTelefonoPerfil = vista.findViewById(R.id.tvTelefonoPerfil)
        tvCiudadPerfil = vista.findViewById(R.id.tvCiudadPerfil)
        tvEstudios = vista.findViewById(R.id.tvEstudios)
        tvExperiencia = vista.findViewById(R.id.tvExperiencia)
        btnEditarPerfil = vista.findViewById(R.id.btnEditarPerfil)
    }

    private fun mostrarDatos() {
        val usuario = Repositorio.usuarioActual ?: return
        tvNombrePerfil.text = usuario.nombre
        tvCargoPerfil.text = usuario.cargo
        tvCorreoPerfil.text = usuario.correo
        tvTelefonoPerfil.text = usuario.telefono.ifBlank { "Sin teléfono registrado" }
        tvCiudadPerfil.text = usuario.ciudad.ifBlank { "Sin ciudad registrada" }
        tvEstudios.text = usuario.estudios.ifBlank {
            "Aún no has registrado tus estudios. Toca «Editar perfil» para agregarlos."
        }
        tvExperiencia.text = usuario.experiencia.ifBlank {
            "Aún no has registrado tu experiencia. Toca «Editar perfil» para agregarla."
        }
        tvEstudios.scrollTo(0, 0)
        tvExperiencia.scrollTo(0, 0)
    }

    /** RF-06: permite modificar la información personal. */
    private fun mostrarDialogoEdicion() {
        val usuario = Repositorio.usuarioActual ?: return
        val vista = layoutInflater.inflate(R.layout.dialog_editar_perfil, null)

        val etEditarTelefono: EditText = vista.findViewById(R.id.etEditarTelefono)
        val etEditarCiudad: EditText = vista.findViewById(R.id.etEditarCiudad)
        val etEditarEstudios: EditText = vista.findViewById(R.id.etEditarEstudios)
        val etEditarExperiencia: EditText = vista.findViewById(R.id.etEditarExperiencia)

        etEditarTelefono.setText(usuario.telefono)
        etEditarCiudad.setText(usuario.ciudad)
        etEditarEstudios.setText(usuario.estudios)
        etEditarExperiencia.setText(usuario.experiencia)

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.btn_editar_perfil)
            .setView(vista)
            .setPositiveButton("Guardar") { _, _ ->
                usuario.telefono = etEditarTelefono.text.toString().trim()
                usuario.ciudad = etEditarCiudad.text.toString().trim()
                usuario.estudios = etEditarEstudios.text.toString().trim()
                usuario.experiencia = etEditarExperiencia.text.toString().trim()
                mostrarDatos()
                Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
