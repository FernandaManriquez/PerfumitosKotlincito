package com.miapp.xanostorekotlin.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.miapp.xanostorekotlin.R
import com.miapp.xanostorekotlin.api.ApiConfig
import com.miapp.xanostorekotlin.databinding.ItemProductBinding
import com.miapp.xanostorekotlin.model.Product

class ProductAdapter(
    private val isAdmin: Boolean,
    private val onItemClick: (Product) -> Unit,
    private val onEdit: (Product) -> Unit,
    private val onDelete: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var products: List<Product> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size

    fun updateData(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvTitle.text = product.name

            val imageUrl = product.images?.firstOrNull()?.let {
                val path = it.url ?: it.path
                if (path?.startsWith("http") == true) path else ApiConfig.storeBaseUrl.substringBefore("/api:") + path
            }

            if (imageUrl != null) {
                Glide.with(itemView.context).load(imageUrl).into(binding.imgProduct)
            } else {
                binding.imgProduct.setImageResource(R.drawable.logo_foreground)
            }

            // Listener para el clic en todo el item
            itemView.setOnClickListener { onItemClick(product) }

            // Visibilidad y listeners para los botones de admin
            if (isAdmin) {
                binding.adminActions.visibility = View.VISIBLE
                binding.btnEdit.setOnClickListener { onEdit(product) }
                binding.btnDelete.setOnClickListener { onDelete(product) }
            } else {
                binding.adminActions.visibility = View.GONE
            }
        }
    }
}
