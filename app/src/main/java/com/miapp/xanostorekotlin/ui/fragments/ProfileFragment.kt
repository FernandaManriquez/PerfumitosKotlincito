package com.miapp.xanostorekotlin.ui.fragments // Declaramos el paquete donde vive este fragmento

import android.content.Intent // Import para crear Intents al navegar entre Activities
import android.os.Bundle // Import para manejar el ciclo de vida y estado guardado
import android.view.LayoutInflater // Import para inflar layouts XML
import android.view.View // Import de la clase View
import android.view.ViewGroup // Import para referencia al contenedor padre
import androidx.fragment.app.Fragment // Import de la clase base Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.miapp.xanostorekotlin.api.TokenManager // Import de nuestro gestor de tokens/usuario
import com.miapp.xanostorekotlin.databinding.FragmentProfileBinding // Import del ViewBinding generado para fragment_profile.xml
import com.miapp.xanostorekotlin.ui.MainActivity // Import de MainActivity para navegar al login tras logout
import com.bumptech.glide.Glide

/**
 * ProfileFragment
 * Muestra los datos básicos del usuario logeado y permite cerrar sesión.
 * Todas las líneas tienen comentarios para fines didácticos.
 */
class ProfileFragment : Fragment() { // Declaramos la clase del fragmento que hereda de Fragment

    private var _binding: FragmentProfileBinding? = null // Referencia mutable al binding (válida entre onCreateView y onDestroyView)
    private val binding get() = _binding!! // Acceso no nulo al binding mientras la vista existe

    override fun onCreateView( // Métodoo para crear/infla la vista del fragmento
        inflater: LayoutInflater, // Inflador para convertir XML en objetos View
        container: ViewGroup?, // Contenedor padre donde se insertará la vista
        savedInstanceState: Bundle? // Estado previamente guardado (no usado aquí)
    ): View { // Retornamos un View
        _binding = FragmentProfileBinding.inflate(inflater, container, false) // Inflamos el layout usando ViewBinding
        return binding.root // Retornamos la raíz del layout inflado
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // Métodoo llamado cuando la vista ya fue creada
        super.onViewCreated(view, savedInstanceState) // Llamamos a la superclase
        val tm = TokenManager(requireContext())
        binding.tvName.text = tm.getUserName()
        binding.tvEmail.text = "Email: ${tm.getUserEmail() ?: ""}"
        binding.tvRut.text = "RUT: ${tm.getUserRut() ?: ""}"
        binding.tvPhone.text = "Celular: ${tm.getUserPhone() ?: ""}"
        binding.tvAddress.text = "Dirección: ${tm.getUserAddress() ?: ""}"

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val svc = com.miapp.xanostorekotlin.api.RetrofitClient.createUserServiceOnStore(requireContext())
                val me = withContext(kotlinx.coroutines.Dispatchers.IO) { svc.getProfile() }
                binding.tvName.text = me.name
                binding.tvEmail.text = "Email: ${me.email ?: ""}"
                binding.tvRut.text = "RUT: ${me.rut ?: ""}"
                binding.tvPhone.text = "Celular: ${me.phone ?: ""}"
                binding.tvAddress.text = "Dirección: ${me.address ?: ""}"
                val role = when {
                    me.isAdmin == true -> "admin"
                    me.role?.lowercase() == "admin" -> "admin"
                    else -> tm.getUserRole()
                }
                com.miapp.xanostorekotlin.api.TokenManager(requireContext()).saveUser(
                    userId = me.id,
                    userName = me.name,
                    userEmail = me.email ?: "",
                    userRole = role,
                    rut = me.rut,
                    phone = me.phone,
                    address = me.address
                )
            } catch (_: Exception) { }
        }

        binding.tvDataHeader.text = "Mis datos"

        val logoUrl = getString(com.miapp.xanostorekotlin.R.string.app_logo_url)
        if (logoUrl.isNotBlank()) {
            Glide.with(this).load(logoUrl).circleCrop().into(binding.imgProfileLogo)
        }

        val isAdmin = tm.getUserRole()?.lowercase() == "admin"
        binding.btnMyOrders.visibility = if (isAdmin) View.GONE else View.VISIBLE

        binding.btnLogout.setOnClickListener { // Asociamos un listener al botón de Cerrar sesión
            tm.clear() // Limpiamos token y datos del usuario de SharedPreferences
            // Creamos un Intent para ir a MainActivity (pantalla de login)
            val intent = Intent(requireContext(), MainActivity::class.java) // Intent explícito hacia MainActivity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) // Limpiamos el back stack para que no se pueda volver con atrás
            startActivity(intent) // Lanzamos la actividad de login
            requireActivity().finish() // Cerramos la HomeActivity actual para completar el logout
        }

        binding.btnMyOrders.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(com.miapp.xanostorekotlin.R.id.fragment_container, UserOrdersFragment())
                .commitAllowingStateLoss()
        }
    }

    override fun onDestroyView() { // Métodoo llamado cuando la vista del fragmento se está destruyendo
        super.onDestroyView() // Llamamos a la superclase
        _binding = null // Liberamos el binding para evitar fugas de memoria
    }
}
