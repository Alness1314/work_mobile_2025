package com.susess.cv360.ui.events

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.susess.cv360.R
import com.susess.cv360.databinding.FragmentEventsBinding
import com.susess.cv360.helpers.SessionManager
import com.susess.cv360.model.events.EventRequest
import com.susess.cv360.ui.pickers.DatetimePickers
import com.susess.cv360.validations.ValidationResult
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EventsFragment: Fragment() {

    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventsViewModel by viewModels()

    private val adapterEvents by lazy {
        ArrayAdapter(requireContext(), R.layout.item_list, mutableListOf<String>())
    }

    private var username: String? = null;

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
        eventViewModel.getCurrentUser()
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
                    Snackbar.make(binding.root, "Bitacora enviada con exito.", Snackbar.LENGTH_SHORT).show()

                    clearForm()
                }
            }

        }
        eventViewModel.formState.observe(viewLifecycleOwner) { formState ->
            when(val result = formState.descriptionResult){
                is ValidationResult.Invalid -> {
                    binding.inputLayoutDescription.error = result.message
                }
                ValidationResult.Valid -> {
                    binding.inputLayoutDescription.error = null
                }
            }

            when(val result = formState.componentResult){
                is ValidationResult.Invalid -> {
                    binding.inputLayoutComponent.error = result.message
                }
                ValidationResult.Valid -> {
                    binding.inputLayoutComponent.error = null
                }
            }

            when(val result = formState.dateResult){
                is ValidationResult.Invalid -> {
                    binding.inputLayoutDate.error = result.message
                }
                ValidationResult.Valid -> {
                    binding.inputLayoutDate.error = null
                }
            }

            when(val result = formState.timeResult){
                is ValidationResult.Invalid -> {
                    binding.inputLayoutTime.error = result.message
                }
                ValidationResult.Valid -> {
                    binding.inputLayoutTime.error = null
                }
            }
        }
    }

    private fun clearForm() {
        binding.inputDescripcion.text?.clear()
        binding.inputDate.text?.clear()
        binding.inputTime.text?.clear()
        binding.inputComponent.text?.clear()
        binding.autoCompleteEvents.text?.clear()
    }

    private fun setupListeners(){
        binding.inputDate.setOnClickListener {
            val datepicker = DatetimePickers()
            datepicker.showDatePicker(binding.inputDate, requireActivity().supportFragmentManager)
        }
        binding.inputTime.setOnClickListener {
            val timepicker = DatetimePickers()
            timepicker.showTimePicker(binding.inputTime, requireActivity().supportFragmentManager)
        }

        binding.buttonSend.setOnClickListener {
            val resquest = genRequest()
            if (eventViewModel.formState.value?.isFormValid == true)
                eventViewModel.sendEvent(resquest)
        }

        binding.inputComponent.doAfterTextChanged {
            eventViewModel.validateFieldEvents("component", binding.inputComponent.text.toString())
        }
        binding.inputDate.doAfterTextChanged {
            eventViewModel.validateFieldEvents("date", binding.inputDate.text.toString())
        }
        binding.inputTime.doAfterTextChanged {
            eventViewModel.validateFieldEvents("time", binding.inputTime.text.toString())
        }
        binding.inputDescripcion.doAfterTextChanged {
            eventViewModel.validateFieldEvents("description", binding.inputDescripcion.text.toString())
        }
        binding.autoCompleteEvents.doAfterTextChanged {
            eventViewModel.validateFieldEvents("typeEvent", binding.autoCompleteEvents.text.toString())
        }
    }

    private fun genRequest(): EventRequest {
        val numEventType = eventViewModel.eventLive.value
            ?.find { it.nombre == binding.autoCompleteEvents.text.toString() }
        val descripcionEvento = binding.inputDescripcion.text.toString()
        val fecha = binding.inputDate.text.toString()
        val hora = binding.inputTime.text.toString()
        val fechaYHoraEvento = "${fecha} ${hora}"
        val identificacionComponenteAlarma = binding.inputComponent.text.toString()
        val tipoEvento = numEventType?.tipo ?: 1
        val usuarioResponsable = eventViewModel.currentUser.value.toString()
        eventViewModel.validateFieldEvents("component", identificacionComponenteAlarma)
        eventViewModel.validateFieldEvents("date", fecha)
        eventViewModel.validateFieldEvents("time", hora)
        eventViewModel.validateFieldEvents("description", descripcionEvento)
        eventViewModel.validateFieldEvents("typeEvent", binding.autoCompleteEvents.text.toString())

        val request = EventRequest().apply {
            fechaYHoraEvento
            usuarioResponsable
            tipoEvento
            descripcionEvento
            identificacionComponenteAlarma
        }
        return request
    }

    private fun showLoading(show: Boolean) {
        binding.layoutProgressEvents.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}