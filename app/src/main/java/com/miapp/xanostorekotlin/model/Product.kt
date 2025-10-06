package com.miapp.xanostorekotlin.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int,
    @SerializedName(value = "name", alternate = ["nombre"]) val name: String,
    @SerializedName(value = "description", alternate = ["descripcion"]) val description: String?,
    @SerializedName(value = "price", alternate = ["precio"]) val price: Double?,
    val stock: Int,
    @SerializedName(value = "brand", alternate = ["marca"]) val brand: String,
    @SerializedName(value = "category", alternate = ["categoria"]) val category: String,
    @SerializedName(value = "images", alternate = ["image", "imagenes"]) val images: List<ProductImage>?,
    @SerializedName("active") val active: Boolean, // Campo clave que faltaba
    @SerializedName(value = "imagenes_paths", alternate = ["image_paths", "paths"]) val imagePaths: List<String>? = null
) : java.io.Serializable
