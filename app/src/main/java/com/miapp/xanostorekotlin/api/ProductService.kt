package com.miapp.xanostorekotlin.api

import com.google.gson.JsonObject
import com.miapp.xanostorekotlin.model.CreateProductResponse
import com.miapp.xanostorekotlin.model.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {

    // --- Endpoints Públicos ---

    @GET("product")
    suspend fun getProductsForClient(): JsonObject

    @GET("product/{product_id}")
    suspend fun getProduct(@Path("product_id") id: Int): Product

    @GET("products/search")
    suspend fun searchProducts(@Query("q") query: String): List<Product>

    // --- Endpoints de Administrador ---
    @GET("admin/products")
    suspend fun getAdminProducts(): List<Product>

    @Multipart
    @POST("admin/product")
    suspend fun createProduct(
        @Part("nombre") name: RequestBody,
        @Part("descripcion") description: RequestBody,
        @Part("precio") price: RequestBody,
        @Part("stock") stock: RequestBody,
        @Part("marca") brand: RequestBody,
        @Part("categoria") category: RequestBody,
        @Part("active") active: RequestBody,
        @Part imagenes: List<MultipartBody.Part>
    ): CreateProductResponse

    // CORREGIDO: El endpoint de edición ahora es Multipart, como el de creación.
    @Multipart
    @PATCH("admin/product/{product_id}")
    suspend fun updateProduct(
        @Path("product_id") id: Int,
        @PartMap fields: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part image: List<MultipartBody.Part> // 'image' en singular, como en tu API
    ): Product

    @DELETE("admin/product/{product_id}")
    suspend fun deleteProduct(@Path("product_id") id: Int)

    @PATCH("admin/product/{product_id}/stock")
    suspend fun updateStock(@Path("product_id") id: Int, @Body fields: Map<String, @JvmSuppressWildcards Any>): Product

}
