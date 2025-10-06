package com.miapp.xanostorekotlin.model

import com.google.gson.annotations.SerializedName

/**
 * User
 * Modelo de usuario que coincide con la respuesta de la API de Xano.
 */
data class User(
    @SerializedName("id")
    val id: Int,

    @SerializedName(value = "name", alternate = ["firstname"])
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("lastname")
    val lastName: String? = null,

    @SerializedName(value = "role", alternate = ["rol"])
    val role: String? = null,

    @SerializedName(value = "is_admin", alternate = ["isAdmin"])
    val isAdmin: Boolean? = null,

    // CORREGIDO: Se usa el campo 'status' (String) que envía la API, en lugar de 'blocked' (Boolean).
    @SerializedName("status")
    val status: String? = null,

    @SerializedName(value = "direccion", alternate = ["address", "shipping_address"])
    val address: String? = null,

    @SerializedName(value = "celular", alternate = ["phone", "telefono"])
    val phone: String? = null,

    @SerializedName(value = "rut", alternate = ["RUT"])
    val rut: String? = null,

    @SerializedName("created_at")
    val createdAt: Long? = null
) : java.io.Serializable // Hacemos la clase Serializable para poder pasarla entre fragments/activities si es necesario.
