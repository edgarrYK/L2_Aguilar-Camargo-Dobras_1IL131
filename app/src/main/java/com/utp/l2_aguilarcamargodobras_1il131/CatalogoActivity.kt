package com.utp.l2_aguilarcamargodobras_1il131
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.utp.l2_aguilarcamargodobras_1il131.Producto
import com.utp.l2_aguilarcamargodobras_1il131.ProductoAdapter

class CatalogoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogo)

        // Recuperar la lista de MainActivity
        val listaProductos = intent.getSerializableExtra("LISTA_PRODUCTOS") as? ArrayList<Producto> ?: arrayListOf()

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

        // Pulsación breve -> aparece toast
        lvProductos.setOnItemClickListener { _, _, position, _ ->
            val producto = listaProductos[position]
            Toast.makeText(this, "Platillo: ${producto.nombre}", Toast.LENGTH_SHORT).show()
        }

        // Pulsación prolongada -> aparece alertDialog
        lvProductos.setOnItemLongClickListener { _, _, position, _ ->
            val p = listaProductos[position]
            android.app.AlertDialog.Builder(this)
                .setTitle("Detalles del Pedido")
                .setMessage("Platillo: ${p.nombre}\nPrecio: $${p.precio}\nCategoría: ${p.categoria}\n\nNotas: ${p.descripcion}")
                .setPositiveButton("Entendido", null)
                .show()
            true // evento fue consumido
        }

        btnRegresar.setOnClickListener { finish() } // Cerrar
    }
}