package com.susess.cv360.ui.operations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.susess.cv360.R
import com.susess.cv360.databinding.FragmentOperationsBinding
import com.susess.cv360.ui.events.EventsViewModel
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
                is OperationsViewModel.UiState.SendDeliveryOps -> {
                    showLoading(false)
                }
                is OperationsViewModel.UiState.SendReceptionOps -> {
                    showLoading(false)
                }
                is OperationsViewModel.UiState.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.root, state.message,
                        Snackbar.LENGTH_LONG).show()
                }
                else -> {}
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

    private fun setupListeners() {}

    private fun showLoading(show: Boolean) {
        binding.layoutProgressOps.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}