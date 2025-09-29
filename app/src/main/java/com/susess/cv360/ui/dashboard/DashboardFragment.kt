package com.susess.cv360.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.susess.cv360.R
import com.susess.cv360.adapters.ModuleAdapter
import com.susess.cv360.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    // <- Hilt-aware ViewModel retrieval
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private lateinit var adapter: ModuleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ModuleAdapter { module ->
            when (module.route) {
                "receptionsFragment" -> findNavController().navigate(R.id.navigation_reception)
                "deliveriesFragment"   -> findNavController().navigate(R.id.navigation_delivery)
                "eventFragment"  -> findNavController().navigate(R.id.navigation_events)
                //"opsVolumetricFragment"    -> findNavController().navigate(R.id.opsVolumetricFragment)
                else -> Snackbar.make(binding.root, "Ruta no configurada: ${module.route}", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.recyclerMenu.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerMenu.adapter = adapter

        // Observadores
        dashboardViewModel.modules.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.textEmpty.isVisible = list.isEmpty()
        }

        dashboardViewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DashboardViewModel.UiState.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is DashboardViewModel.UiState.Success -> {
                    binding.progressBar.isVisible = false
                }
                is DashboardViewModel.UiState.Error -> {
                    binding.progressBar.isVisible = false
                    Snackbar.make(binding.root, "Error: ${state.message}", Snackbar.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }

        // Cargar datos
        dashboardViewModel.loadModules()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}