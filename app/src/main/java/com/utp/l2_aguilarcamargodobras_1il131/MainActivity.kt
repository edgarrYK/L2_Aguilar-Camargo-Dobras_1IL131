package com.utp.l2_aguilarcamargodobras_1il131

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.content.Intent
import java.io.Serializable

// data class para items
data class Producto(
    val nombre: String,
    val precio: Double,
    val descripcion: String,
    val categoria: String
) : Serializable

// ArrayAdapter para lista
class ProductoAdapter(
    context: android.content.Context,
    private val productos: List<Producto>,
    private val categoryImages: Map<String, Int>
) : ArrayAdapter<Producto>(context, 0, productos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: android.view.LayoutInflater.from(context)
            .inflate(R.layout.item_producto, parent, false)

        val producto = productos[position]

        val ivImagen = view.findViewById<ImageView>(R.id.ivItemImagen)
        val tvNombre = view.findViewById<TextView>(R.id.tvItemNombre)
        val tvSecundario = view.findViewById<TextView>(R.id.tvItemSecundario)

        tvNombre.text = producto.nombre
        tvSecundario.text = "Precio: $${String.format("%.2f", producto.precio)} — ${producto.categoria}"

        val imgRes = categoryImages[producto.categoria] ?: android.R.drawable.ic_menu_gallery
        ivImagen.setImageResource(imgRes)

        return view
    }
}

// MainActivity
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

    private val categoryImages = mapOf(
        "Hamburguesa" to R.drawable.ic_burger,
        "Sushi"       to R.drawable.ic_sushi,
        "Pizza"       to R.drawable.ic_pizza,
        "Ensalada"    to R.drawable.ic_salad,
        "Bebida"      to R.drawable.ic_drink
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


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

        btnAgregar.setOnClickListener(this)
        btnVerCatalogo.setOnClickListener(this)

        btnAgregar.isEnabled = false
    }

    // Spinner  ArrayAdapter
    private fun setupSpinner() {
        val categorias = listOf("Seleccione una categoría", "Hamburguesa", "Sushi", "Pizza", "Ensalada", "Bebida")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categorias
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spinnerCategoria.adapter = adapter

        spinnerCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val categoriaSeleccionada = parent.getItemAtPosition(position).toString()

                if (position == 0) {
                    ivProductImage.setImageResource(R.drawable.ic_placeholder)
                } else {
                    val imgRes = categoryImages[categoriaSeleccionada] ?: R.drawable.ic_placeholder
                    ivProductImage.setImageResource(imgRes)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                ivProductImage.setImageResource(R.drawable.ic_placeholder)
            }
        }
    }

    // CheckBox controles
    private fun setupCheckBox() {
        cbConfirmar.setOnCheckedChangeListener { _, isChecked ->
            btnAgregar.isEnabled = isChecked
            btnAgregar.alpha = if (isChecked) 1f else 0.5f
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAgregar     -> agregarProducto()
            R.id.btnVerCatalogo -> verCatalogo()
        }
    }


    private fun mostrarDialogoDetalles(producto: Producto) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Detalles del Producto")
        builder.setMessage(
            "Nombre: ${producto.nombre}\n" +
                    "Precio: $${producto.precio}\n" +
                    "Categoría: ${producto.categoria}\n" +
                    "Descripción: ${producto.descripcion}"
        )
        builder.setPositiveButton("Cerrar", null)
        builder.show()
    }


    // validación
    private fun agregarProducto() {
        val nombre      = etNombre.text.toString().trim()
        val precioStr   = etPrecio.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()
        val categoria = spinnerCategoria.selectedItem?.toString() ?: ""
        val categoriaPosicion = spinnerCategoria.selectedItemPosition

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

        //  agregar al catalogo
        val producto = Producto(
            nombre      = nombre,
            precio      = precioStr.toDouble(),
            descripcion = descripcion,
            categoria   = categoria
        )

        if (categoriaPosicion == 0) {
            Toast.makeText(this, "Por favor, seleccione una categoría válida", Toast.LENGTH_SHORT).show()
            hayError = true
        }

        if (hayError) return

        catalogoProductos.add(producto)

        tvProductCount.text = "${catalogoProductos.size} producto(s)"

        // Reset
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
            Toast.makeText(this, "No hay productos registrados", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, CatalogoActivity::class.java)
        intent.putExtra("LISTA_PRODUCTOS", ArrayList(catalogoProductos))
        startActivity(intent)
        }
    }
