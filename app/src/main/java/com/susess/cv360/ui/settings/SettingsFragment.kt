package com.susess.cv360.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
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
        settingsViewModel.loadFacilities(sessionManager.authHeaders(), sessionManager.username!!)
        settingsViewModel.loadDefaults()
    }

    private fun setupObservers() {
        settingsViewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SettingsViewModel.UiState.Idle -> showLoading(false)
                is SettingsViewModel.UiState.Loading -> showLoading(true)
                is SettingsViewModel.UiState.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                }
                is SettingsViewModel.UiState.DefaultsCfgLoaded -> {
                    state.settingCfg?.let { cfg ->
                        binding.textFacilitySet.text = cfg.facilityName
                        binding.textTankSet.text = cfg.tankName
                        binding.textProductSet.text = cfg.productName
                        binding.textUnitMeasurementSet.text = cfg.unitMeasurement
                    }
                }
                is SettingsViewModel.UiState.SettingCfgSaved -> {
                    showLoading(false)
                    Snackbar.make(binding.root, "Configuración guardada", Snackbar.LENGTH_SHORT).show()
                }

            }
        }

        settingsViewModel.facilities.observe(viewLifecycleOwner) { list ->
            adapterFacilities.clear()
            adapterFacilities.addAll(list.map { it.externalKey })
            adapterFacilities.notifyDataSetChanged()
            adapterFacilities.filter.filter(null)
            binding.autoCompleteFacility.setText("", false)
        }

        settingsViewModel.tanks.observe(viewLifecycleOwner) { list ->
            adapterTanks.clear()
            adapterTanks.addAll(list.map { it.externalKey })
            adapterTanks.notifyDataSetChanged()
            adapterTanks.filter.filter(null)
            binding.autoCompleteTanks.setText("", false)
            binding.inputLayoutTanks.isEnabled = true
        }
    }

    private fun setupListeners() {
        binding.autoCompleteFacility.setOnItemClickListener { _, _, position, _ ->
            val facility = settingsViewModel.facilities.value?.get(position)
            facility?.let {
                settingsViewModel.loadTanks(sessionManager.authHeaders(), it.publicKey)
                binding.inputLayoutTanks.isEnabled = false
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
            val facility = settingsViewModel.facilities.value
                ?.find { it.externalKey == binding.autoCompleteFacility.text.toString() }
            val tank = settingsViewModel.tanks.value
                ?.find { it.externalKey == binding.autoCompleteTanks.text.toString() }

            if (facility != null && tank != null) {
                settingsViewModel.saveSettings(facility, tank)
                binding.textFacilitySet.text = facility.externalKey
                binding.textTankSet.text = tank.externalKey
                binding.textProductSet.text = tank.producto.marcaComercial
                binding.textUnitMeasurementSet.text = tank.producto.unidadMedida
            } else {
                Snackbar.make(binding.root, "Debes seleccionar instalación y tanque", Snackbar.LENGTH_SHORT).show()
            }

        }
    }

    private fun showLoading(show: Boolean) {
        binding.layoutProgresBarSettings.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}