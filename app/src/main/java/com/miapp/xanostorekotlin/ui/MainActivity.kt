package com.miapp.xanostorekotlin.ui // Paquete donde vive la Activity de login

import android.content.Context
import android.content.Intent // Import para navegar a otra Activity
import android.os.Bundle // Import para ciclo de vida y estado
import android.util.Log
import android.view.View // Import para manipular visibilidad de vistas
import android.widget.Toast // Import para notificaciones cortas
import androidx.appcompat.app.AppCompatActivity // Activity base compatible
import androidx.lifecycle.lifecycleScope // Alcance de corrutinas ligado al ciclo de vida
import com.miapp.xanostorekotlin.api.RetrofitClient // Cliente Retrofit centralizado
import com.miapp.xanostorekotlin.api.TokenManager // Gestor de token/usuario
import com.miapp.xanostorekotlin.databinding.ActivityMainBinding // ViewBinding del layout activity_main.xml
import com.miapp.xanostorekotlin.model.LoginRequest // Modelo para enviar email y password
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers // Dispatcher para correr en IO
import kotlinx.coroutines.launch // Lanzar corrutinas
import kotlinx.coroutines.withContext // Cambiar contexto dentro de corrutinas

/**
 * MainActivity (Login)
 *
 * Explicación:
 * - Muestra un formulario de email y password.
 * - Al presionar el botón, llama al endpoint de login usando corrutinas.
 * - Si el login es exitoso, guarda el token y datos del usuario y navega a HomeActivity.
 * - Se utiliza ViewBinding para acceder a las vistas y lifecycleScope para las corrutinas.
 */
class MainActivity : AppCompatActivity() { // Activity principal de login

    private lateinit var binding: ActivityMainBinding // Referencia a ViewBinding para acceder a vistas
    private lateinit var tokenManager: TokenManager // Manejador de sesión/token del usuario

    override fun onCreate(savedInstanceState: Bundle?) { // Ciclo de vida: creación de la Activity
        super.onCreate(savedInstanceState) // Llamamos al métodoo base
        binding = ActivityMainBinding.inflate(layoutInflater) // Inflamos el layout con ViewBinding
        setContentView(binding.root) // Establecemos el contenido de la Activity

        tokenManager = TokenManager(this) // Inicializamos TokenManager con contexto

        val logoUrl = getString(com.miapp.xanostorekotlin.R.string.app_logo_url)
        if (logoUrl.isNotBlank()) {
            Glide.with(this).load(logoUrl).circleCrop().into(binding.imgLogo)
        }

        // Si ya hay sesión, vamos directo a Home
        if (tokenManager.isLoggedIn()) { // Consultamos si hay token guardado
            goToHome() // Navegamos a Home
            return // Terminamos onCreate para no mostrar login
        }

        binding.btnLogin.setOnClickListener { // Click en botón Login
            val email = binding.etEmail.text?.toString()?.trim().orEmpty() // Obtenemos email
            val password = binding.etPassword.text?.toString()?.trim().orEmpty() // Obtenemos password

            binding.tilEmail.error = null
            binding.tilPassword.error = null
            val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            var hasError = false
            if (!emailValid) {
                binding.tilEmail.error = "Email inválido"
                hasError = true
            }
            if (password.isBlank()) {
                binding.tilPassword.error = "Password requerido"
                hasError = true
            }
            if (hasError) return@setOnClickListener

            // Mostramos progreso
            binding.progress.visibility = View.VISIBLE // Indicador visible
            binding.btnLogin.isEnabled = false // Bloqueamos botón para evitar múltiples clics

            // Corrutina para llamar a la API de login
            lifecycleScope.launch {
                try {
                    // --- FASE 1: LOGIN (usando el servicio PÚBLICO) ---
                    // Llamamos a createAuthService sin el segundo parámetro (o con 'false'),
                    // para obtener un servicio sin token.
                    val publicAuthService = RetrofitClient.createAuthService(this@MainActivity)
                    val loginResponse = withContext(Dispatchers.IO) {
                        publicAuthService.login(LoginRequest(email = email, password = password))
                    }

                    val authToken = loginResponse.resolvedToken()
                    if (authToken != null) {
                        tokenManager.saveToken(authToken)

                        val privateAuthService = RetrofitClient.createAuthService(this@MainActivity, requiresAuth = true)
                        val userProfile = withContext(Dispatchers.IO) { privateAuthService.getMe() }

                        val resolvedRole = when {
                            userProfile.isAdmin == true -> "admin"
                            userProfile.role?.lowercase() == "admin" -> "admin"
                            else -> "cliente"
                        }

                        tokenManager.saveAuth(
                            token = authToken,
                            userId = userProfile.id,
                            userName = userProfile.name,
                            userEmail = userProfile.email,
                            userRole = resolvedRole,
                            rut = userProfile.rut,
                            phone = userProfile.phone,
                            address = userProfile.address
                        )

                        Toast.makeText(this@MainActivity, "¡Bienvenido, ${userProfile.name}!", Toast.LENGTH_SHORT).show()
                        goToHome()
                    } else {
                        val u = loginResponse.user
                            ?: throw IllegalStateException("Login exitoso sin token ni usuario")
                        val role = u.role?.lowercase() ?: "cliente"
                        tokenManager.saveUser(
                            userId = u.id,
                            userName = u.name,
                            userEmail = u.email,
                            userRole = role,
                            rut = u.rut,
                            phone = u.phone,
                            address = u.address
                        )
                        Toast.makeText(this@MainActivity, "¡Bienvenido, ${u.name}!", Toast.LENGTH_SHORT).show()
                        goToHome()
                    }

                } catch (e: Exception) {
                    Log.e("MainActivity", "Login o GetProfile error", e)
                    val raw = e.message ?: ""
                    fun extract(key: String): String? {
                        val tag = "\"$key\":\""
                        val i = raw.indexOf(tag)
                        if (i < 0) return null
                        val j = raw.indexOf('"', i + tag.length)
                        return if (j > i) raw.substring(i + tag.length, j) else null
                    }
                    val code = extract("code")?.uppercase()
                    val msg = extract("message")
                    val friendly = when {
                        code == "ERROR_CODE_UNAUTHORIZED" || raw.contains("401") -> "Autenticación requerida. Inicia sesión."
                        code == "ERROR_CODE_NOT_FOUND" || raw.contains("404") -> "Ruta no encontrada en el backend."
                        !msg.isNullOrBlank() -> msg
                        raw.contains("Invalid Credentials", true) || raw.contains("403") -> "Credenciales inválidas. Verifica email y contraseña."
                        else -> "Error: ${e.message}"
                    }
                    Toast.makeText(this@MainActivity, friendly, Toast.LENGTH_LONG).show()
                    if (
                        raw.contains("Invalid Credentials", true) ||
                        raw.contains("401") || raw.contains("Unauthorized", true)
                    ) {
                        tokenManager.clear()
                    }
                } finally {
                    binding.progress.visibility = View.GONE // Indicador invisible
                    binding.btnLogin.isEnabled = true
                }
            }
        }

        binding.btnGoSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun goToHome() { // Navegar a la pantalla de Home
        val intent = Intent(this, HomeActivity::class.java) // Creamos el Intent explícito
        startActivity(intent) // Lanzamos la nueva Activity
        finish() // Cerramos la Activity actual para no volver con back
    }
}
