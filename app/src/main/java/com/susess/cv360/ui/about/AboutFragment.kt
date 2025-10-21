package com.susess.cv360.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.susess.cv360.databinding.FragmentAboutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    private val aboutViewModel: AboutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupObservers()
        aboutViewModel.loadAbout();
    }

    private fun setupObservers() {
        aboutViewModel.uiState.observe(viewLifecycleOwner){ state ->
            when(state) {
                is AboutViewModel.UiState.Idle -> showLoading(false)
                is AboutViewModel.UiState.Loading -> showLoading(true)
                is AboutViewModel.UiState.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                }
                is AboutViewModel.UiState.AboutLoaded -> {
                    showLoading(false)
                    state.about?.let { about ->
                        binding.textApplication.text = about.name
                        binding.textNombre.text = about.shortName
                        binding.textVersion.text = about.version
                        binding.textDerechos.text = about.copyright
                        binding.textLicencia.text = about.contribuyente?.razonSocial
                        binding.textProveedor.text = about.serviceProvider
                        binding.textNumeroSerie.text = about.numeroLicencia
                    }
                }
            }
        }

        // Observamos los eventos de navegación
        aboutViewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is AboutViewModel.NavigationEvent.ToDashboard -> {
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.layoutProgressAbout.visibility = if(show) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}