package com.miapp.xanostorekotlin.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.miapp.xanostorekotlin.R
import com.miapp.xanostorekotlin.api.RetrofitClient
import com.miapp.xanostorekotlin.api.TokenManager
import com.miapp.xanostorekotlin.databinding.FragmentProductsBinding
import com.miapp.xanostorekotlin.model.Product
import com.miapp.xanostorekotlin.ui.ProductDetailActivity
import com.miapp.xanostorekotlin.ui.adapter.ProductAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductsFragment : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProductAdapter
    private var allProducts: MutableList<Product> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        setupSearch()
        loadProducts()
    }

    private fun setupRecycler() {
        val isAdmin = TokenManager(requireContext()).getUserRole()?.lowercase() == "admin"
        adapter = ProductAdapter(
            isAdmin = isAdmin,
            onItemClick = { product ->
                val intent = Intent(requireContext(), ProductDetailActivity::class.java).apply {
                    putExtra("PRODUCT_ID", product.id)
                }
                startActivity(intent)
            },
            onEdit = { product ->
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProductEditFragment.new(product.id))
                    .addToBackStack(null)
                    .commit()
            },
            onDelete = { product ->
                showDeleteConfirmation(product)
            }
        )
        binding.recyclerProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerProducts.adapter = adapter
    }

    private fun showDeleteConfirmation(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Producto")
            .setMessage("¿Estás seguro de que deseas eliminar '${product.name}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteProduct(product)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteProduct(product: Product) {
        val productIndex = allProducts.indexOf(product)
        if (productIndex == -1) return

        allProducts.removeAt(productIndex)
        adapter.updateData(allProducts)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val productService = RetrofitClient.createProductService(requireContext())
                withContext(Dispatchers.IO) {
                    productService.deleteProduct(product.id)
                }
            } catch (e: Exception) {
                binding.tvError.text = "Error: No se pudo eliminar '${product.name}'"
                allProducts.add(productIndex, product)
                adapter.updateData(allProducts)
            }
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })
    }

    private fun filter(query: String?) {
        val q = query?.trim()?.lowercase()
        if (q.isNullOrEmpty()) {
            adapter.updateData(allProducts)
            return
        }
        val filteredList = allProducts.filter {
            it.name.lowercase().contains(q) || (it.category?.lowercase()?.contains(q) ?: false)
        }
        adapter.updateData(filteredList)
    }

    private fun loadProducts() {
        binding.progress.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE
        binding.tvError.text = ""
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val service = RetrofitClient.createProductService(requireContext())
                val isAdmin = TokenManager(requireContext()).getUserRole()?.lowercase() == "admin"

                val products = withContext(Dispatchers.IO) {
                    if (isAdmin) {
                        service.getAdminProducts()
                    } else {
                        // Para clientes, llama al endpoint que devuelve un objeto y extrae la lista "items"
                        val response = service.getProductsForClient()
                        val itemsArray = response.getAsJsonArray("items")
                        Gson().fromJson(itemsArray, Array<Product>::class.java).toList()
                    }
                }
                allProducts = products.toMutableList()
                adapter.updateData(allProducts)
                binding.tvEmpty.visibility = if (products.isEmpty()) View.VISIBLE else View.GONE
            } catch (e: Exception) {
                Log.e("ProductsFragment", "Error cargando productos", e)
                binding.tvError.text = "Error cargando productos: ${e.message}"
                binding.tvEmpty.visibility = if (allProducts.isEmpty()) View.VISIBLE else View.GONE
            } finally {
                binding.progress.visibility = View.GONE
            }
        }
        binding.btnRetry.setOnClickListener { loadProducts() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
