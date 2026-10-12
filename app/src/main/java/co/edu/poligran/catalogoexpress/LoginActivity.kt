package co.edu.poligran.catalogoexpress

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/** Pantalla de inicio de sesión (RF-02). */
class LoginActivity : AppCompatActivity() {

    // Variables vinculadas a las vistas del layout activity_login.xml
    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var cbRecordarme: CheckBox
    private lateinit var tvOlvideContrasena: TextView
    private lateinit var btnIniciarSesion: Button
    private lateinit var tvRegistrate: TextView

    private lateinit var preferencias: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        preferencias = getSharedPreferences("catalogo_express", Context.MODE_PRIVATE)
        vincularVistas()
        cargarCorreoRecordado()
        configurarEventos()
    }

    /** Vincula cada variable con el identificador de su vista. */
    private fun vincularVistas() {
        etCorreo = findViewById(R.id.etCorreo)
        etContrasena = findViewById(R.id.etContrasena)
        cbRecordarme = findViewById(R.id.cbRecordarme)
        tvOlvideContrasena = findViewById(R.id.tvOlvideContrasena)
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)
        tvRegistrate = findViewById(R.id.tvRegistrate)
    }

    /** Declara los eventos de la pantalla. */
    private fun configurarEventos() {
        btnIniciarSesion.setOnClickListener { iniciarSesion() }

        tvRegistrate.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }

        tvOlvideContrasena.setOnClickListener {
            Toast.makeText(
                this,
                "La recuperación de contraseña estará disponible en una próxima versión.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun iniciarSesion() {
        val correo = etCorreo.text.toString().trim()
        val contrasena = etContrasena.text.toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.error = "Ingresa un correo válido"
            etCorreo.requestFocus()
            return
        }
        if (contrasena.isEmpty()) {
            etContrasena.error = "Ingresa tu contraseña"
            etContrasena.requestFocus()
            return
        }

        val error = Repositorio.iniciarSesion(correo, contrasena)
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            return
        }

        guardarCorreoRecordado(correo)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    /** Si el usuario marcó "Recordarme", se muestra su correo al abrir la app. */
    private fun cargarCorreoRecordado() {
        val correo = preferencias.getString("correo_recordado", "") ?: ""
        if (correo.isNotEmpty()) {
            etCorreo.setText(correo)
            cbRecordarme.isChecked = true
        }
    }

    private fun guardarCorreoRecordado(correo: String) {
        val editor = preferencias.edit()
        if (cbRecordarme.isChecked) {
            editor.putString("correo_recordado", correo)
        } else {
            editor.remove("correo_recordado")
        }
        editor.apply()
    }
}
