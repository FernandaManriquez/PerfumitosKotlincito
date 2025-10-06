package com.miapp.xanostorekotlin.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.miapp.xanostorekotlin.databinding.ItemImageSliderBinding

class ImageSliderAdapter(private val images: List<String>) :
    RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {

    // El ViewHolder contiene la vista de una sola imagen del carrusel.
    inner class ImageViewHolder(val binding: ItemImageSliderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        // Obtenemos la URL de la imagen en la posición actual.
        val imageUrl = images[position]
        // Usamos Coil para cargar la imagen en el ImageView.
        Glide.with(holder.binding.imageView)
            .load(imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_dialog_alert)
            .into(holder.binding.imageView)
    }

    // El carrusel tendrá tantas páginas como imágenes haya en la lista.
    override fun getItemCount(): Int = images.size
}