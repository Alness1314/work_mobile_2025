package com.susess.cv360.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.susess.cv360.R
import com.susess.cv360.databinding.FragmentSettingsBinding
import com.susess.cv360.helpers.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sessionManager: SessionManager

    private val settingsViewModel: SettingsViewModel by viewModels()

    private lateinit var adapterFacilities: ArrayAdapter<String>
    private lateinit var adapterTanks: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapterFacilities = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf())
        adapterTanks = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf())

        binding.autoCompleteFacility.setAdapter(adapterFacilities)
        binding.autoCompleteTanks.setAdapter(adapterTanks)

        setupObservers()
        setupListeners()
        settingsViewModel.loadFacilities(authHeaders(), sessionManager.username!!)
    }

    private fun setupObservers() {
        settingsViewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SettingsViewModel.UiState.Idle -> Unit
                is SettingsViewModel.UiState.Loading -> showLoading(true)
                is SettingsViewModel.UiState.FacilitiesLoaded -> {
                    showLoading(false)
                    adapterFacilities.clear()
                    adapterFacilities.addAll(state.facilities.map { it.externalKey })
                    adapterFacilities.notifyDataSetChanged()
                }

                is SettingsViewModel.UiState.TanksLoaded -> {
                    binding.autoCompleteTanks.isEnabled = true
                    showLoading(false)
                    adapterTanks.clear()
                    adapterTanks.addAll(state.tanks.map { it.externalKey })
                    adapterFacilities.notifyDataSetChanged()
                }

                is SettingsViewModel.UiState.DefaultsCfgLoaded -> TODO()
                is SettingsViewModel.UiState.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                }

                is SettingsViewModel.UiState.SettingCfgSaved -> TODO()

            }
        }

        settingsViewModel.facilities.observe(viewLifecycleOwner) { list ->
            //adapterFacilities.clear()
            //adapterFacilities.addAll(list.map { it.externalKey })
            //adapterFacilities.notifyDataSetChanged()

            // 1. AÑADIR ESTA LÍNEA para resetear el texto de Facility
            //binding.autoCompleteFacility.setText("", false)

            /*binding.autoCompleteFacility.post {
                if (adapterFacilities.count > 0) {
                    binding.autoCompleteFacility.showDropDown()
                }
            }*/

            // *** CAMBIO CLAVE: Crear y asignar un NUEVO adaptador ***
            val newAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line, // Usando el default de Android
                list.map { it.externalKey } // Los datos
            )
            binding.autoCompleteFacility.setAdapter(newAdapter)

            binding.autoCompleteFacility.setText("", false)
        }

        settingsViewModel.tanks.observe(viewLifecycleOwner) { list ->
            //adapterTanks.clear()
            //adapterTanks.addAll(list.map { it.externalKey })
            //adapterTanks.notifyDataSetChanged()
            val newAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line, // Usando el default de Android
                list.map { it.externalKey } // Los datos
            )
            binding.autoCompleteTanks.setAdapter(newAdapter)

            // 2. AÑADIR ESTA LÍNEA para resetear el texto de Tanks
            binding.autoCompleteTanks.setText("", false)

            /*binding.autoCompleteTanks.post {
                if (adapterTanks.count > 0) {
                    binding.autoCompleteTanks.showDropDown()
                }
            }*/

            // 3. (Opcional) Deshabilitar Tanks hasta que se seleccione Facility, si esa es tu lógica
            binding.autoCompleteTanks.isEnabled = false
        }
    }

    private fun setupListeners() {
        binding.autoCompleteFacility.setOnItemClickListener { _, _, position, _ ->
            val facility = settingsViewModel.facilities.value?.get(position)
            facility?.let {
                settingsViewModel.loadTanks(authHeaders(), it.publicKey)
                binding.autoCompleteTanks.isEnabled = true
            }
        }

        binding.autoCompleteTanks.setOnItemClickListener { parent, view, position, id ->
            val tank = settingsViewModel.tanks.value?.get(position)
            tank?.let {
                binding.inputProduct.setText(it.producto.marcaComercial)
                binding.inputUnitMeasurement.setText(it.producto.unidadMedida)
                binding.buttonSelectTank.isEnabled = true
            }
        }

        binding.buttonSelectTank.setOnClickListener {
            val selected = adapterTanks.getPosition(binding.autoCompleteTanks.text.toString())
            val tank = settingsViewModel.uiState.value.let {
                if (it is SettingsViewModel.UiState.TanksLoaded) {
                    it.tanks.getOrNull(selected)
                } else null
            }
            tank?.let {
                Log.i("seleccion tanque: ", tank.toString())
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBarSettings.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun authHeaders() =
        mapOf("Authorization" to "Bearer ${sessionManager.token}")

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}