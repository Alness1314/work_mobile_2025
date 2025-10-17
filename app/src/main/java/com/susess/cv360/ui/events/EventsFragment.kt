package com.susess.cv360.ui.events

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.susess.cv360.R
import com.susess.cv360.databinding.FragmentEventsBinding
import com.susess.cv360.model.events.EventRequest
import com.susess.cv360.ui.pickers.DatetimePickers
import com.susess.cv360.validations.ValidationResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventsFragment: Fragment() {
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
        eventViewModel.checkConfig()
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

            when(val result = formState.typeEventResult){
                is ValidationResult.Invalid -> {
                    binding.inputLayoutTypeEvents.error = result.message
                }
                ValidationResult.Valid -> {
                    binding.inputLayoutTypeEvents.error = null
                }
            }
        }

        eventViewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is EventsViewModel.NavigationEventAbout.ToDashboard -> {
                    findNavController().navigate(R.id.action_navigation_events_to_navigation_dashboard)
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
            val formState = eventViewModel.formState.value!!
            Log.i("EVENTS", formState.isFormValid.toString())
            if(formState.isFormValid){
                eventViewModel.sendEvent(resquest)
            }else{
                Snackbar.make(binding.root, "Por favor ingrese todos los campos.", Snackbar.LENGTH_SHORT).show()
            }

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
        binding.autoCompleteEvents.apply {
            doAfterTextChanged {
                eventViewModel.validateFieldEvents("typeEvent", text.toString())
            }
            setOnItemClickListener { _, _, _, _ ->
                val selected = text.toString()
                eventViewModel.validateFieldEvents("typeEvent", selected)
            }
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

        val request = EventRequest(
            fechaYHoraEvento = fechaYHoraEvento,
            descripcionEvento = descripcionEvento,
            tipoEvento = tipoEvento,
            identificacionComponenteAlarma = identificacionComponenteAlarma,
            usuarioResponsable = usuarioResponsable,
        )
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