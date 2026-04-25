package com.utp.l2_aguilarcamargodobras_1il131

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CatalogoActivity : AppCompatActivity() {

    private lateinit var listaProductos: MutableList<Producto>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogo)

        listaProductos = DataHolder.catalogoProductos

        val lvProductos = findViewById<ListView>(R.id.lvProductos)
        val btnRegresar = findViewById<Button>(R.id.btnRegresar)

        val categoryImages = mapOf(
            "Hamburguesa" to R.drawable.ic_burger,
            "Sushi"       to R.drawable.ic_sushi,
            "Pizza"       to R.drawable.ic_pizza,
            "Ensalada"    to R.drawable.ic_salad,
            "Bebida"      to R.drawable.ic_drink
        )

        val adapter = ProductoAdapter(this, listaProductos, categoryImages)
        lvProductos.adapter = adapter

        lvProductos.setOnItemClickListener { _, _, position, _ ->
            val producto = listaProductos[position]
            Toast.makeText(this, "Platillo: ${producto.nombre}", Toast.LENGTH_SHORT).show()
        }

        lvProductos.setOnItemLongClickListener { _, _, position, _ ->
            val p = listaProductos[position]
            val currentAdapter = lvProductos.adapter as ProductoAdapter

            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Detalles: ${p.nombre}")
                .setMessage("Precio: $${p.precio}\nCategoría: ${p.categoria}\n\nNotas: ${p.descripcion}")
                .setNeutralButton("Eliminar") { _, _ ->
                    listaProductos.removeAt(position)
                    currentAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Editar") { _, _ ->
                    abrirEditorRapido(p, position, currentAdapter)
                }
                .setPositiveButton("Cerrar", null)
                .show()
            true
        }

        btnRegresar.setOnClickListener { finish() }
    }

    private fun abrirEditorRapido(p: Producto, position: Int, adapter: ProductoAdapter) {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(60, 40, 60, 10)

        val editNombre = EditText(this).apply { hint = "Nombre"; setText(p.nombre) }
        val editPrecio = EditText(this).apply {
            hint = "Precio"; setText(p.precio.toString())
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        val editDesc = EditText(this).apply { hint = "Descripción"; setText(p.descripcion) }

        val spinnerEdit = Spinner(this)
        val categorias = listOf("Hamburguesa", "Sushi", "Pizza", "Ensalada", "Bebida")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEdit.adapter = spinnerAdapter

        val currentCatPos = categorias.indexOf(p.categoria)
        if (currentCatPos >= 0) spinnerEdit.setSelection(currentCatPos)

        layout.addView(TextView(this).apply { text = "Nombre:" })
        layout.addView(editNombre)
        layout.addView(TextView(this).apply { text = "Precio:" })
        layout.addView(editPrecio)
        layout.addView(TextView(this).apply { text = "Categoría:" })
        layout.addView(spinnerEdit)
        layout.addView(TextView(this).apply { text = "Descripción:" })
        layout.addView(editDesc)

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Editar Producto")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val nNombre = editNombre.text.toString()
                val nPrecio = editPrecio.text.toString().toDoubleOrNull() ?: p.precio
                val nCat = spinnerEdit.selectedItem.toString()
                val nDesc = editDesc.text.toString()

                if (nNombre.isNotEmpty()) {
                    // Actualización directa sobre la lista de DataHolder
                    listaProductos[position] = Producto(nNombre, nPrecio, nDesc, nCat)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}