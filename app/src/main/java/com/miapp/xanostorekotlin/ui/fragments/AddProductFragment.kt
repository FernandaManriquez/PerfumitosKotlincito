package com.miapp.xanostorekotlin.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.miapp.xanostorekotlin.api.RetrofitClient
import com.miapp.xanostorekotlin.api.TokenManager
import com.miapp.xanostorekotlin.databinding.FragmentAddProductBinding
import com.miapp.xanostorekotlin.ui.adapter.ImagePreviewAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    private val selectedImageUris = mutableListOf<Uri>()
    private lateinit var imagePreviewAdapter: ImagePreviewAdapter

    private val pickImages = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            selectedImageUris.clear()
            selectedImageUris.addAll(uris)
            imagePreviewAdapter.notifyDataSetChanged()
            binding.rvImagePreview.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tm = TokenManager(requireContext())
        if (tm.getUserRole()?.lowercase() != "admin") {
            Toast.makeText(requireContext(), "Requiere permisos de administrador", Toast.LENGTH_LONG).show()
            parentFragmentManager.popBackStack()
            return
        }
        setupRecyclerView()

        binding.btnSelectImage.setOnClickListener {
            pickImages.launch("image/*")
        }
        binding.btnSubmit.setOnClickListener {
            submit()
        }
    }

    private fun setupRecyclerView() {
        imagePreviewAdapter = ImagePreviewAdapter(selectedImageUris)
        binding.rvImagePreview.adapter = imagePreviewAdapter
    }

    private fun submit() {
        val name = binding.etName.text?.toString()?.trim().orEmpty()
        val description = binding.etDescription.text?.toString()?.trim().orEmpty()
        val priceText = binding.etPrice.text?.toString()?.trim()?.replace(',', '.')?.replace(" ", "")
        val price = priceText?.toDoubleOrNull()
        val stock = binding.etStock.text?.toString()?.trim()?.toIntOrNull()
        val brand = binding.etBrand.text?.toString()?.trim()
        val category = binding.etCategory.text?.toString()?.trim()

        if (name.isBlank() || price == null || stock == null || brand.isNullOrBlank() || category.isNullOrBlank()) {
            Toast.makeText(context, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progress.visibility = View.VISIBLE
        binding.btnSubmit.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
                val pricePart = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val stockPart = stock.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val brandPart = brand.toRequestBody("text/plain".toMediaTypeOrNull())
                val categoryPart = category.toRequestBody("text/plain".toMediaTypeOrNull())
                val activePart = "true".toRequestBody("text/plain".toMediaTypeOrNull()) // <-- CAMPO AÑADIDO

                val imageParts = selectedImageUris.mapNotNull { uri ->
                    try {
                        val inputStream = requireContext().contentResolver.openInputStream(uri)
                        val bytes = inputStream?.readBytes() ?: return@mapNotNull null
                        inputStream.close()
                        val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("imagenes", "image.jpg", requestFile)
                    } catch (e: Exception) {
                        Log.e("AddProductFragment", "Error processing image URI: $uri", e)
                        null
                    }
                }

                val service = RetrofitClient.createProductService(requireContext())
                withContext(Dispatchers.IO) {
                    service.createProduct(
                        name = namePart,
                        description = descriptionPart,
                        price = pricePart,
                        stock = stockPart,
                        brand = brandPart,
                        category = categoryPart,
                        active = activePart, // <-- CAMPO AÑADIDO
                        imagenes = imageParts
                    )
                }

                Toast.makeText(requireContext(), "¡Producto creado con éxito!", Toast.LENGTH_LONG).show()
                clearForm()

            } catch (e: Exception) {
                Log.e("AddProductFragment", "Error al crear el producto", e)
                Toast.makeText(requireContext(), "Error creando producto: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progress.visibility = View.GONE
                binding.btnSubmit.isEnabled = true
            }
        }
    }

    private fun clearForm() {
        binding.etName.text?.clear()
        binding.etDescription.text?.clear()
        binding.etPrice.text?.clear()
        binding.etStock.text?.clear()
        binding.etBrand.text?.clear()
        binding.etCategory.text?.clear()
        selectedImageUris.clear()
        imagePreviewAdapter.notifyDataSetChanged()
        binding.rvImagePreview.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
