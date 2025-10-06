package com.miapp.xanostorekotlin.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("authToken") val authToken: String? = null,
    @SerializedName("token") val token: String? = null,
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("jwt") val jwt: String? = null,
    @SerializedName("authorization") val authorization: String? = null,
    @SerializedName("code") val code: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("user") val user: User? = null
) {
    fun resolvedToken(): String? {
        val cands = listOf(authToken, token, accessToken, jwt, authorization)
        for (raw in cands) {
            val v = raw?.trim()
            if (!v.isNullOrBlank()) return v
        }
        return null
    }
}