package com.miapp.xanostorekotlin.api // Paquete donde declaramos el gestor de token/usuario

import android.content.Context // Import para acceder a recursos del sistema y preferencias
import android.content.SharedPreferences // Import de la interfaz SharedPreferences

/**
 * TokenManager
 * Responsable de guardar y leer el token y datos básicos del usuario en SharedPreferences.
 *
 * Nota para estudiantes:
 * - Esto NO es seguro para producción (se recomienda EncryptedSharedPreferences o DataStore + cifrado).
 * - Para este proyecto educativo, es suficiente para mantener la sesión.
 */
class TokenManager(context: Context) { // Clase que encapsula el acceso a SharedPreferences
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ¡¡CAMBIO AQUÍ!!
    // Esta variable contendrá el token en memoria durante el proceso de login.
    var currentToken: String? = null
        private set // La hacemos de solo lectura desde fuera

    init {
        // Al iniciar, cargamos el token desde las preferencias
        currentToken = prefs.getString(KEY_TOKEN, null)
    }

    fun saveAuth(token: String, userId: Int, userName: String, userEmail: String, userRole: String?, rut: String? = null, phone: String? = null, address: String? = null) {
        currentToken = token // Actualizamos la variable en memoria
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, userName)
            putString(KEY_USER_EMAIL, userEmail)
            putString(KEY_USER_ROLE, userRole)
            putString(KEY_USER_RUT, rut)
            putString(KEY_USER_PHONE, phone)
            putString(KEY_USER_ADDRESS, address)
            apply()
        }
    }

    fun saveUser(userId: Int, userName: String, userEmail: String, userRole: String?, rut: String? = null, phone: String? = null, address: String? = null) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, userName)
            putString(KEY_USER_EMAIL, userEmail)
            putString(KEY_USER_ROLE, userRole)
            putString(KEY_USER_RUT, rut)
            putString(KEY_USER_PHONE, phone)
            putString(KEY_USER_ADDRESS, address)
            apply()
        }
    }

    fun getUserRut(): String? = prefs.getString(KEY_USER_RUT, null)
    fun getUserPhone(): String? = prefs.getString(KEY_USER_PHONE, null)
    fun getUserAddress(): String? = prefs.getString(KEY_USER_ADDRESS, null)

    // El interceptor usará este métodoo, que ahora devolverá el token en memoria si existe.
    fun getToken(): String? {
        val mem = currentToken
        if (!mem.isNullOrBlank()) return mem
        return prefs.getString(KEY_TOKEN, null)
    }

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, 0)
    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    fun getUserRole(): String? = prefs.getString(KEY_USER_ROLE, null)
    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()

    fun clear() {
        currentToken = null // Limpiamos la variable en memoria
        prefs.edit().clear().apply()
    }

    fun saveToken(token: String) {
        val cleaned = token.trim()
        if (cleaned.isBlank()) {
            clear()
        } else {
            currentToken = cleaned
            prefs.edit().putString(KEY_TOKEN, cleaned).apply()
        }
    }

    companion object {
        private const val PREFS_NAME = "session"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_RUT = "user_rut"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_USER_ADDRESS = "user_address"
    }
}