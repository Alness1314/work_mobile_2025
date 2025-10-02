package com.susess.cv360.ui.events

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.susess.cv360.R
import com.susess.cv360.databinding.FragmentEventsBinding
import com.susess.cv360.model.events.EventRequest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventsFragment : Fragment() {

    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventsViewModel by viewModels()

    private val adapterEvents by lazy {
        ArrayAdapter(requireContext(), R.layout.item_list, mutableListOf<String>())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.autoCompleteEvents.setAdapter(adapterEvents)
        setupObservers()
        eventViewModel.loadTypeEvents()
        setupListeners()
    }

    private fun setupObservers(){
        eventViewModel.uiState.observe(viewLifecycleOwner) { state ->
            when(state) {
                EventsViewModel.UiState.Idle -> Unit
                EventsViewModel.UiState.Loading -> showLoading(true)
                is EventsViewModel.UiState.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                }
                is EventsViewModel.UiState.EventsLoaded -> {
                    showLoading(false)
                    adapterEvents.clear()
                    adapterEvents.addAll(state.listEvents.map { it.nombre })
                    adapterEvents.notifyDataSetChanged()
                }
                is EventsViewModel.UiState.SendEventApi -> {
                    showLoading(false)
                    Snackbar.make(binding.root, "Configuración guardada", Snackbar.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun setupListeners(){
        binding.buttonSend.setOnClickListener {
            if (validateFields(binding.inputLayoutDescription, binding.inputLayoutComponent, binding.inputLayoutDate, binding.inputLayoutTime)) {
                val numEventType = eventViewModel.eventLive.value
                    ?.find { it.nombre == binding.autoCompleteEvents.text.toString() }
                val request = EventRequest().apply {
                    descripcionEvento = binding.inputDescripcion.text.toString()
                    fechaYHoraEvento = "${binding.inputDate.text} ${binding.inputTime.text}"
                    identificacionComponenteAlarma = binding.inputComponent.text.toString()
                    tipoEvento = numEventType?.tipo ?: 1 // aquí obtienes el seleccionado
                    usuarioResponsable = "usuario" // UsernameManager
                }
                eventViewModel.sendEvent(request)
            }
        }
    }

    private fun validateFields(vararg textFields: TextInputLayout): Boolean {
        var isValid = true
        for (textField in textFields) {
            if (textField.editText?.text.toString().trim().isEmpty()) {
                textField.error = getString(R.string.helper_required)
                isValid = false
            } else textField.error = null
        }
        return isValid
    }

    private fun showLoading(show: Boolean) {
        binding.layoutProgressEvents.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}