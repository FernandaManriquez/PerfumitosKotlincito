package com.miapp.xanostorekotlin.api // Paquete del servicio de autenticación

import com.miapp.xanostorekotlin.model.AuthResponse // Import del modelo de respuesta de login
import com.miapp.xanostorekotlin.model.LoginRequest // Import del modelo de request de login
import com.miapp.xanostorekotlin.model.SignupRequest
import com.miapp.xanostorekotlin.model.User
import retrofit2.http.Body // Import de anotación para cuerpo de la solicitud
import retrofit2.http.GET
import retrofit2.http.POST // Import de anotación para métodoo HTTP POST

/**
 * AuthService
 * Define el endpoint de login (y potencialmente logout) de la API de Xano.
 * Base URL usada: ApiConfig.authBaseUrl
 * Todas las líneas están comentadas para fines didácticos.
 */
interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/signup")
    suspend fun signup(@Body request: Map<String, @JvmSuppressWildcards Any>): AuthResponse

    @GET("auth/me")
    suspend fun getMe(): User
}
