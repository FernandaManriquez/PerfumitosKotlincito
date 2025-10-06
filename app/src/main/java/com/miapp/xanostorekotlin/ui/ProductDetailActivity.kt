package com.miapp.xanostorekotlin.ui // Define el paquete al que pertenece esta clase.

import android.os.Build // Importa la clase Build para verificar la versión de Android del dispositivo.
import android.os.Bundle // Importa la clase Bundle, usada para pasar datos entre actividades.
import android.view.MenuItem // Importa la clase MenuItem para identificar los botones de la barra de acción.
import androidx.appcompat.app.AppCompatActivity // Importa la clase base para actividades que usan la barra de acción de compatibilidad.
import com.miapp.xanostorekotlin.databinding.ActivityProductDetailBinding // Importa la clase de ViewBinding generada para nuestro layout.
import com.miapp.xanostorekotlin.model.Product // Importa nuestro modelo de datos 'Product'.
import com.miapp.xanostorekotlin.ui.adapter.ImageSliderAdapter // Importa el adaptador que creamos para el carrusel de imágenes.
import com.miapp.xanostorekotlin.manager.CartManager
import com.miapp.xanostorekotlin.model.CartItem
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.miapp.xanostorekotlin.api.RetrofitClient
import kotlin.math.roundToInt

/**
 * ProductDetailActivity
 * Esta actividad muestra los detalles completos de un solo producto,
 * incluyendo un carrusel de imágenes y un botón para volver atrás.
 */
class ProductDetailActivity : AppCompatActivity() { // La clase hereda de AppCompatActivity.

    // Declara una variable para el ViewBinding que se inicializará más tarde (lateinit).
    private lateinit var binding: ActivityProductDetailBinding

    // Este métodoo se llama cuando la actividad se crea por primera vez.
    override fun onCreate(savedInstanceState: Bundle?) {
        // Llama al métodoo onCreate de la clase padre.
        super.onCreate(savedInstanceState)
        // Infla el layout usando ViewBinding y asigna la referencia a nuestra variable 'binding'.
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        // Establece la vista de contenido de la actividad a la vista raíz de nuestro layout inflado.
        setContentView(binding.root)
        binding.imageViewPager.adapter = ImageSliderAdapter(emptyList())

        // --- HABILITAR EL BOTÓN DE "VOLVER ATRÁS" ---
        // Accede a la barra de acción (ActionBar) de soporte y, si existe (?.), habilita la visualización del botón "home".
        // El botón "home" se muestra como una flecha de retroceso (<-) por defecto.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val pid = intent.getIntExtra("PRODUCT_ID", 0)
        if (pid > 0) {
            lifecycleScope.launch {
                try {
                    val svc = RetrofitClient.createProductService(this@ProductDetailActivity)
                    val p = withContext(Dispatchers.IO) { svc.getProduct(pid) }
                    android.util.Log.d("ProductDetailActivity", "Producto ${p.id} images=${p.images?.size ?: 0}")
                    setupUI(p)
                } catch (e: Exception) {
                    title = "Producto"
                    binding.tvProductName.text = "Producto no disponible"
                    binding.tvProductPrice.text = "Precio no disponible"
                    binding.tvProductDescription.text = "Error al cargar: ${e.message ?: "desconocido"}"
                    binding.tvProductStock.text = "Stock: No especificado"
                    binding.fabAddToCart.isEnabled = false
                }
            }
        } else {
            finish()
        }
    }

    // Este métodoo configura todos los elementos de la interfaz de usuario con los datos del producto.
    private fun setupUI(product: Product) {
        // --- TÍTULO DE LA ACTIVIDAD ---
        // Establece el título de la barra de acción (ActionBar) con el nombre del producto.
        title = product.name

        // --- CONFIGURAR LOS TEXTOS ---
        // Asigna el nombre del producto al TextView correspondiente.
        binding.tvProductName.text = "Nombre: ${product.name}"
        // Asigna el precio. Usa 'let' para formatear el texto solo si el precio no es nulo. Si es nulo, muestra un texto alternativo.
        binding.tvProductPrice.text = product.price?.let { "Precio: $it" } ?: "Precio no disponible"
        // Asigna la descripción. Si es nula, usa el operador Elvis (?:) para mostrar "Sin descripción.".
        binding.tvProductDescription.text = "Descripción: ${product.description ?: "Sin descripción."}" // Pequeña mejora aquí también
        // Asigna el stock usando una plantilla de string.
        binding.tvProductStock.text = "Stock: ${product.stock ?: "No especificado"}" // Pequeoñ mejora

        // --- CONFIGURAR EL CARRUSEL DE IMÁGENES ---
        // Usa 'let' para ejecutar este bloque solo si la lista de imágenes del producto no es nula.
        run {
            val origin = com.miapp.xanostorekotlin.api.ApiConfig.storeBaseUrl.substringBefore("/api:")
            val urlsFromObjects = product.images?.mapNotNull { img ->
                val raw = img.url ?: img.path
                val clean = raw?.replace("`", "")?.trim()
                clean?.let { c -> if (c.startsWith("http")) c else origin + (if (c.startsWith("/")) c else "/$c") }
            } ?: emptyList()
            val urlsFromPaths = product.imagePaths?.mapNotNull { p ->
                val clean = p.replace("`", "").trim()
                if (clean.isNotBlank()) { if (clean.startsWith("http")) clean else origin + (if (clean.startsWith("/")) clean else "/$clean") } else null
            } ?: emptyList()
            val imageUrls = if (urlsFromObjects.isNotEmpty()) urlsFromObjects else urlsFromPaths
            if (imageUrls.isNotEmpty()) {
                binding.imageViewPager.adapter = ImageSliderAdapter(imageUrls)
            }
        }

        binding.fabAddToCart.setOnClickListener {
            val cm = CartManager(this)
            if (product.stock <= 0) return@setOnClickListener
            val origin = com.miapp.xanostorekotlin.api.ApiConfig.storeBaseUrl.substringBefore("/api:")
            val firstUrl = run {
                val fromObjects = product.images?.firstOrNull()?.let { pi ->
                    val raw = pi.url ?: pi.path
                    val clean = raw?.replace("`", "")?.trim()
                    clean?.let { c -> if (c.startsWith("http")) c else origin + (if (c.startsWith("/")) c else "/$c") }
                }
                val fromPaths = product.imagePaths?.firstOrNull()?.let { p ->
                    val clean = p.replace("`", "").trim()
                    if (clean.startsWith("http")) clean else origin + (if (clean.startsWith("/")) clean else "/$clean")
                }
                fromObjects ?: fromPaths
            }
            cm.addOrIncrement(
                CartItem(
                    productId = product.id,
                    name = product.name,
                    brand = product.brand,
                    price = product.price?.roundToInt() ?: 0,
                    stock = product.stock,
                    quantity = 1,
                    imageUrl = firstUrl
                )
            )
        }
    }

    // --- MANEJAR EL CLIC EN EL BOTÓN "VOLVER ATRÁS" ---
    // Este métodoo se llama automáticamente cuando se presiona un botón de la barra de acción.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Comprueba si el ID del item presionado es 'android.R.id.home'.
        // Este es el ID estándar para el botón de la flecha de retroceso (Up button).
        if (item.itemId == android.R.id.home) {
            // Si es el botón de retroceso, finaliza la actividad actual.
            // Esto destruye ProductDetailActivity y regresa a la actividad anterior en la pila (HomeActivity).
            finish()
            // Devuelve 'true' para indicar que hemos manejado el evento de clic con éxito.
            return true
        }
        // Si el botón presionado no es el que nos interesa, pasamos el evento al métodoo de la clase padre para que lo maneje.
        return super.onOptionsItemSelected(item)
    }
}
