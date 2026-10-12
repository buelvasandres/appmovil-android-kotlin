package co.edu.poligran.catalogoexpress

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/** Pantalla de registro (RF-01) que evita correos duplicados (RF-04). */
class RegistroActivity : AppCompatActivity() {

    // Variables vinculadas a las vistas del layout activity_registro.xml
    private lateinit var btnVolver: ImageButton
    private lateinit var etNombre: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etCorreoRegistro: EditText
    private lateinit var etContrasenaRegistro: EditText
    private lateinit var etConfirmar: EditText
    private lateinit var cbTerminos: CheckBox
    private lateinit var btnRegistrarse: Button
    private lateinit var tvIrLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        vincularVistas()
        configurarEventos()
    }

    private fun vincularVistas() {
        btnVolver = findViewById(R.id.btnVolver)
        etNombre = findViewById(R.id.etNombre)
        etTelefono = findViewById(R.id.etTelefono)
        etCorreoRegistro = findViewById(R.id.etCorreoRegistro)
        etContrasenaRegistro = findViewById(R.id.etContrasenaRegistro)
        etConfirmar = findViewById(R.id.etConfirmar)
        cbTerminos = findViewById(R.id.cbTerminos)
        btnRegistrarse = findViewById(R.id.btnRegistrarse)
        tvIrLogin = findViewById(R.id.tvIrLogin)
    }

    private fun configurarEventos() {
        btnVolver.setOnClickListener { finish() }
        tvIrLogin.setOnClickListener { finish() }
        btnRegistrarse.setOnClickListener { registrar() }
    }

    private fun registrar() {
        val nombre = etNombre.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val correo = etCorreoRegistro.text.toString().trim()
        val contrasena = etContrasenaRegistro.text.toString()
        val confirmacion = etConfirmar.text.toString()

        // Validaciones de los datos ingresados
        if (nombre.isEmpty()) {
            etNombre.error = "El nombre es obligatorio"
            etNombre.requestFocus()
            return
        }
        if (telefono.isNotEmpty() && !telefono.matches(Regex("[0-9 +]{7,15}"))) {
            etTelefono.error = "Teléfono no válido"
            etTelefono.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreoRegistro.error = "Correo no válido"
            etCorreoRegistro.requestFocus()
            return
        }
        if (contrasena.length < 6) {
            etContrasenaRegistro.error = "Mínimo 6 caracteres"
            etContrasenaRegistro.requestFocus()
            return
        }
        if (contrasena != confirmacion) {
            etConfirmar.error = "Las contraseñas no coinciden"
            etConfirmar.requestFocus()
            return
        }
        if (!cbTerminos.isChecked) {
            Toast.makeText(this, "Debes aceptar los términos y condiciones", Toast.LENGTH_SHORT).show()
            return
        }

        val error = Repositorio.registrar(
            Usuario(
                nombre = nombre,
                correo = correo,
                contrasena = contrasena,
                telefono = telefono
            )
        )
        if (error != null) {
            etCorreoRegistro.error = error
            etCorreoRegistro.requestFocus()
            return
        }

        Toast.makeText(this, "Cuenta creada. Ya puedes iniciar sesión.", Toast.LENGTH_LONG).show()
        finish()
    }
}
