package co.edu.poligran.catalogoexpress

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment

/** Fragmento Web: carga la página cuya URL se escribe en la caja de texto. */
class WebFragment : Fragment(R.layout.fragment_web) {

    // Variables vinculadas a las vistas del layout fragment_web.xml
    private lateinit var etUrl: EditText
    private lateinit var btnIr: Button
    private lateinit var btnAtras: ImageButton
    private lateinit var btnRecargar: ImageButton
    private lateinit var btnInicio: ImageButton
    private lateinit var pbCarga: ProgressBar
    private lateinit var wvPagina: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vincularVistas(view)

        wvPagina.settings.javaScriptEnabled = true
        wvPagina.settings.domStorageEnabled = true

        // Mantiene la navegación dentro de la aplicación y actualiza la URL
        wvPagina.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (url != null) {
                    etUrl.setText(url)
                }
            }
        }

        // Muestra el progreso de carga en la barra
        wvPagina.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                pbCarga.progress = newProgress
                pbCarga.visibility = if (newProgress < 100) View.VISIBLE else View.INVISIBLE
            }
        }

        configurarEventos()

        if (savedInstanceState == null || wvPagina.restoreState(savedInstanceState) == null) {
            etUrl.setText(PAGINA_INICIO)
            cargarPagina()
        }
    }

    private fun vincularVistas(vista: View) {
        etUrl = vista.findViewById(R.id.etUrl)
        btnIr = vista.findViewById(R.id.btnIr)
        btnAtras = vista.findViewById(R.id.btnAtras)
        btnRecargar = vista.findViewById(R.id.btnRecargar)
        btnInicio = vista.findViewById(R.id.btnInicio)
        pbCarga = vista.findViewById(R.id.pbCarga)
        wvPagina = vista.findViewById(R.id.wvPagina)
    }

    private fun configurarEventos() {
        btnIr.setOnClickListener { cargarPagina() }

        // Tecla "Ir" del teclado
        etUrl.setOnEditorActionListener { _, accion, _ ->
            if (accion == EditorInfo.IME_ACTION_GO) {
                cargarPagina()
                true
            } else {
                false
            }
        }

        btnAtras.setOnClickListener {
            if (wvPagina.canGoBack()) {
                wvPagina.goBack()
            } else {
                Toast.makeText(requireContext(), "No hay páginas anteriores", Toast.LENGTH_SHORT).show()
            }
        }

        btnRecargar.setOnClickListener { wvPagina.reload() }

        btnInicio.setOnClickListener {
            etUrl.setText(PAGINA_INICIO)
            cargarPagina()
        }
    }

    /** Valida la dirección escrita y la carga en el WebView. */
    private fun cargarPagina() {
        var url = etUrl.text.toString().trim()
        if (url.isEmpty()) {
            etUrl.error = "Escribe una dirección web"
            return
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://$url"
        }
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            etUrl.error = "La dirección no es válida"
            return
        }
        ocultarTeclado()
        wvPagina.loadUrl(url)
    }

    private fun ocultarTeclado() {
        val teclado = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        teclado.hideSoftInputFromWindow(etUrl.windowToken, 0)
        etUrl.clearFocus()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::wvPagina.isInitialized) {
            wvPagina.saveState(outState)
        }
    }

    companion object {
        private const val PAGINA_INICIO = "https://www.poligran.edu.co"
    }
}
