package com.susess.cv360.ui.operations

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.susess.cv360.R
import com.susess.cv360.databinding.FragmentOperationsBinding
import com.susess.cv360.helpers.RealPathUtil
import com.susess.cv360.ui.events.EventsViewModel
import com.susess.cv360.ui.pickers.DatetimePickers
import com.susess.cv360.validations.ValidationResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OperationsFragment : Fragment() {
    private var _binding: FragmentOperationsBinding? = null
    private val binding get() = _binding!!

    private val operationsViewModel: OperationsViewModel by viewModels()

    private val adapterOps by lazy {
        ArrayAdapter(requireContext(), R.layout.item_list, mutableListOf<String>())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOperationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.autoCompleteOperations.setAdapter(adapterOps)
        setupObservers()
        operationsViewModel.checkConfig()
        operationsViewModel.loadOperationsArray()
        setupListeners()
    }

    private fun setupObservers() {
        operationsViewModel.uiState.observe(viewLifecycleOwner){ state ->
            when(state){
                is OperationsViewModel.UiState.Loading -> {
                    showLoading(true)
                }
                is OperationsViewModel.UiState.OperationsLoaded -> {
                    showLoading(false)
                    adapterOps.clear()
                    adapterOps.addAll(state.operations.map { it })
                    adapterOps.notifyDataSetChanged()
                }
                is OperationsViewModel.UiState.FileUploaded -> {
                    showLoading(false)
                    Snackbar.make(binding.root, "Archivo cargado con exito.", Snackbar.LENGTH_SHORT).show()
                }
                is OperationsViewModel.UiState.SendDeliveryOps -> {
                    showLoading(false)
                    Snackbar.make(binding.root, "Descarga enviada con exito.", Snackbar.LENGTH_SHORT).show()
                }
                is OperationsViewModel.UiState.SendReceptionOps -> {
                    showLoading(false)
                    Snackbar.make(binding.root, "Carga enviada con exito.", Snackbar.LENGTH_SHORT).show()
                }
                is OperationsViewModel.UiState.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.root, state.message,
                        Snackbar.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
        operationsViewModel.fileResponse.observe(viewLifecycleOwner) { response ->
            response?.publicKey?.let { publicKey ->
                binding.inputUploadFile.setText(publicKey)
            }
        }
        operationsViewModel.formState.observe(viewLifecycleOwner) { formState ->
            when(val result = formState.operationResult){
                is ValidationResult.Invalid -> {
                    binding.inputLayoutTransaction.error = result.message
                }
                ValidationResult.Valid -> {
                    binding.inputLayoutTransaction.error = null
                }
            }

            when(val result = formState.volumenResult){
                is ValidationResult.Invalid -> {
                    binding.inputLayoutVolume.error = result.message
                }
                ValidationResult.Valid -> {
                    binding.inputLayoutVolume.error = null
                }
            }

            when(val result = formState.dateStartResult){
                is ValidationResult.Invalid -> {
                    binding.inputLayoutStartDate.error = result.message
                }
                ValidationResult.Valid -> {
                    binding.inputLayoutStartDate.error = null
                }
            }

            when(val result = formState.dateEndResult){
                is ValidationResult.Invalid -> {
                    binding.inputLayoutEndDate.error = result.message
                }
                ValidationResult.Valid -> {
                    binding.inputLayoutEndDate.error = null
                }
            }

            when(val result = formState.timeStartResult){
                is ValidationResult.Invalid -> {
                    binding.inputLayoutStartTime.error = result.message
                }
                ValidationResult.Valid -> {
                    binding.inputLayoutStartTime.error = null
                }
            }

            when(val result = formState.timeEndResult){
                is ValidationResult.Invalid -> {
                    binding.inputLayoutEndTime.error = result.message
                }
                ValidationResult.Valid -> {
                    binding.inputLayoutEndTime.error = null
                }
            }

            when(val result = formState.fileResult){
                is ValidationResult.Invalid -> {
                    binding.inputLayoutUploadFile.error = result.message
                }
                ValidationResult.Valid -> {
                    binding.inputLayoutUploadFile.error = null
                }
            }
        }
        operationsViewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is OperationsViewModel.NavigationEventOps.ToDashboard -> {
                    findNavController().navigate(
                        R.id.action_navigation_operations_to_navigation_dashboard)
                }
            }
        }
    }

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) {
        uri ->
        uri?.let {
            val path = RealPathUtil.getRealPath(requireContext(), it)
            if (path != null) {
                operationsViewModel.uploadFile(path)
            } else {
                Snackbar.make(binding.root, "No se pudo obtener la ruta del archivo", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.buttonUploadSave.setOnClickListener {
            filePickerLauncher.launch(arrayOf("image/*"))
        }

        binding.inputStartDate.setOnClickListener {
            val datepicker = DatetimePickers()
            datepicker.showDatePicker(binding.inputStartDate, requireActivity().supportFragmentManager)
        }
        binding.inputStartTime.setOnClickListener {
            val timepicker = DatetimePickers()
            timepicker.showTimePicker(binding.inputStartTime, requireActivity().supportFragmentManager)
        }
        binding.inputEndTime.setOnClickListener {
            val timepicker = DatetimePickers()
            timepicker.showTimePicker(binding.inputEndTime, requireActivity().supportFragmentManager)
        }
        binding.inputEndDate.setOnClickListener {
            val datepicker = DatetimePickers()
            datepicker.showDatePicker(binding.inputEndDate, requireActivity().supportFragmentManager)
        }

        binding.buttonSendOps.setOnClickListener {
            with(binding) {
                val operationType = operationsViewModel.operationsList.value
                    ?.find { it == autoCompleteOperations.text.toString() }
                val startDate = inputStartDate.text.toString()
                val endDate = inputEndDate.text.toString()
                val startTime = inputStartTime.text.toString()
                val endTime = inputEndTime.text.toString()
                val volume = inputVolume.text.toString()

                Log.i("OperationsFragment", "Operation type: $operationType")
                Log.i("OperationsFragment", "startDateTime: $startDate $startTime")
                Log.i("OperationsFragment", "endDateTime: $endDate $endTime")
                Log.i("OperationsFragment", "volume: $volume")

                operationsViewModel.processOperation(
                    operationType!!, startDate,
                    startTime, endDate, endTime, volume
                )
            }
        }
        binding.autoCompleteOperations.apply {
            doAfterTextChanged {
                operationsViewModel.validateFieldEvents("operation", text.toString())
            }
            setOnItemClickListener { _, _, _, _ ->
                val selected = text.toString()
                operationsViewModel.validateFieldEvents("operation", selected)
            }
        }

        binding.inputVolume.doAfterTextChanged {
            operationsViewModel.validateFieldEvents("volume", binding.inputVolume.text.toString())
        }
                                                                                                                                                                                                                                                                                                 
    }

    private fun showLoading(show: Boolean) {
        binding.layoutProgressOps.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}