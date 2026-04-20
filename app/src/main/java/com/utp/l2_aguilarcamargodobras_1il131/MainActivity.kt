package com.utp.l2_aguilarcamargodobras_1il131

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

// ── Data class for catalog items ──────────────────────────────────────────────
data class Producto(
    val nombre: String,
    val precio: Double,
    val descripcion: String,
    val categoria: String
)

// ── Custom ArrayAdapter for the catalog list ──────────────────────────────────
class ProductoAdapter(
    context: android.content.Context,
    private val productos: List<Producto>
) : ArrayAdapter<Producto>(context, android.R.layout.simple_list_item_2, productos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
            ?: android.view.LayoutInflater.from(context)
                .inflate(android.R.layout.simple_list_item_2, parent, false)
        val producto = productos[position]
        view.findViewById<TextView>(android.R.id.text1).text = producto.nombre
        view.findViewById<TextView>(android.R.id.text2).text =
            "$${producto.precio} — ${producto.categoria}"
        return view
    }
}

// ── MainActivity ──────────────────────────────────────────────────────────────
class MainActivity : AppCompatActivity(), View.OnClickListener {

    // Views
    private lateinit var etNombre: EditText
    private lateinit var etPrecio: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var spinnerCategoria: Spinner
    private lateinit var ivProductImage: ImageView
    private lateinit var cbConfirmar: CheckBox
    private lateinit var btnAgregar: Button
    private lateinit var btnVerCatalogo: Button
    private lateinit var tvProductCount: TextView

    // Data
    private val catalogoProductos = mutableListOf<Producto>()

    // Category → drawable resource map
    // Replace the drawable names with your actual image resources
    private val categoryImages = mapOf(
        "Laptop"     to android.R.drawable.ic_menu_agenda,
        "Smartphone" to android.R.drawable.ic_menu_call,
        "Tablet"     to android.R.drawable.ic_menu_crop,
        "Accesorio"  to android.R.drawable.ic_menu_compass,
        "Monitor"    to android.R.drawable.ic_menu_camera
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Bind views
        etNombre       = findViewById(R.id.etNombre)
        etPrecio       = findViewById(R.id.etPrecio)
        etDescripcion  = findViewById(R.id.etDescripcion)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        ivProductImage = findViewById(R.id.ivProductImage)
        cbConfirmar    = findViewById(R.id.cbConfirmar)
        btnAgregar     = findViewById(R.id.btnAgregar)
        btnVerCatalogo = findViewById(R.id.btnVerCatalogo)
        tvProductCount = findViewById(R.id.tvProductCount)

        setupSpinner()
        setupCheckBox()

        // Wire OnClickListener (activity implements it)
        btnAgregar.setOnClickListener(this)
        btnVerCatalogo.setOnClickListener(this)

        // Disabled until checkbox is ticked
        btnAgregar.isEnabled = false
    }

    // ── Spinner with ArrayAdapter ─────────────────────────────────────────────
    private fun setupSpinner() {
        val categorias = listOf("Laptop", "Smartphone", "Tablet", "Accesorio", "Monitor")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categorias
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spinnerCategoria.adapter = adapter

        // ImageView updates based on selected category
        spinnerCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                val categoria = categorias[position]
                val imgRes = categoryImages[categoria] ?: android.R.drawable.ic_menu_gallery
                ivProductImage.setImageResource(imgRes)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // ── CheckBox controls button activation ──────────────────────────────────
    private fun setupCheckBox() {
        cbConfirmar.setOnCheckedChangeListener { _, isChecked ->
            btnAgregar.isEnabled = isChecked
            btnAgregar.alpha = if (isChecked) 1f else 0.5f
        }
    }

    // ── OnClickListener implementation ───────────────────────────────────────
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAgregar     -> agregarProducto()
            R.id.btnVerCatalogo -> verCatalogo()
        }
    }

    // ── Form validation using setError() ─────────────────────────────────────
    private fun agregarProducto() {
        val nombre      = etNombre.text.toString().trim()
        val precioStr   = etPrecio.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()
        val categoria   = spinnerCategoria.selectedItem?.toString() ?: ""

        var hayError = false

        if (nombre.isEmpty()) {
            etNombre.error = "El nombre no puede estar vacío"
            hayError = true
        }

        if (precioStr.isEmpty()) {
            etPrecio.error = "Ingrese un precio"
            hayError = true
        } else if (precioStr.toDoubleOrNull() == null || precioStr.toDouble() <= 0) {
            etPrecio.error = "Ingrese un precio válido"
            hayError = true
        }

        if (descripcion.isEmpty()) {
            etDescripcion.error = "La descripción no puede estar vacía"
            hayError = true
        }

        if (hayError) return

        // All valid — add to catalog
        val producto = Producto(
            nombre      = nombre,
            precio      = precioStr.toDouble(),
            descripcion = descripcion,
            categoria   = categoria
        )
        catalogoProductos.add(producto)

        // Update counter
        tvProductCount.text = "${catalogoProductos.size} producto(s)"

        // Reset form
        etNombre.text.clear()
        etPrecio.text.clear()
        etDescripcion.text.clear()
        spinnerCategoria.setSelection(0)
        cbConfirmar.isChecked = false
        btnAgregar.isEnabled = false
        btnAgregar.alpha = 0.5f

        Toast.makeText(this, "Producto agregado al catálogo", Toast.LENGTH_SHORT).show()
    }

    private fun verCatalogo() {
        if (catalogoProductos.isEmpty()) {
            Toast.makeText(this, "El catálogo está vacío", Toast.LENGTH_SHORT).show()
            return
        }

        // Build summary string for an AlertDialog
        val adapter = ProductoAdapter(this, catalogoProductos)
        val listView = ListView(this).apply { this.adapter = adapter }

        android.app.AlertDialog.Builder(this)
            .setTitle("Catálogo (${catalogoProductos.size} productos)")
            .setView(listView)
            .setPositiveButton("Cerrar", null)
            .show()
    }
}