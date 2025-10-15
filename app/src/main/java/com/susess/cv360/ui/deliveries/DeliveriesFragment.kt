package com.susess.cv360.ui.deliveries

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.susess.cv360.R
import com.susess.cv360.adapters.DeliveryAdapter
import com.susess.cv360.adapters.ReceptionAdapter
import com.susess.cv360.databinding.FragmentDeliveriesBinding
import com.susess.cv360.ui.pickers.DatetimePickers
import com.susess.cv360.ui.receptions.ReceptionViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDate

@AndroidEntryPoint
class DeliveriesFragment : Fragment() {
    private var _binding: FragmentDeliveriesBinding? = null
    private val binding get() = _binding!!

    private val devliveriesViewModel: DeliveriesViewModel by viewModels()
    private lateinit var adapter: DeliveryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDeliveriesBinding.inflate(inflater, container, false)
       return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecycler()
        setupObservers()
        setupListeners()
        initValues()
    }

    fun setupRecycler(){
        adapter = DeliveryAdapter{}
        binding.recyclerDeliveries.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerDeliveries.adapter = adapter
    }

    fun setupObservers(){
        devliveriesViewModel.deliveries.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }

        devliveriesViewModel.uiState.observe(viewLifecycleOwner){ state ->
            when (state) {
                is DeliveriesViewModel.UiState.Loading -> {
                    showLoading(true)
                }
                is DeliveriesViewModel.UiState.Success -> {
                    showLoading(false)
                }
                is DeliveriesViewModel.UiState.Error -> {
                    showLoading(false)
                    Snackbar.make(binding.root, "Error: ${state.message}", Snackbar.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }

    }

    fun initValues(){
        val date = LocalDate.now().toString()
        binding.inputDateStart.setText(date)
        binding.inputDateEnd.setText(date)
        devliveriesViewModel.loadDeliveries(startDate = date, endDate = date)
    }

    fun setupListeners(){
        binding.inputDateStart.setOnClickListener {
            val datepicker = DatetimePickers()
            datepicker.showDatePicker(binding.inputDateStart, requireActivity().supportFragmentManager)
        }
        binding.inputDateEnd.setOnClickListener {
            val datepicker = DatetimePickers()
            datepicker.showDatePicker(binding.inputDateEnd, requireActivity().supportFragmentManager)
        }

        binding.buttonSearch.setOnClickListener {
            val startDate = binding.inputDateStart.text.toString()
            val endDate = binding.inputDateEnd.text.toString()
            devliveriesViewModel.loadDeliveries(startDate = startDate, endDate = endDate)
        }
    }

    private fun showLoading(show: Boolean) {
        binding.layoutProgressDeliveries.visibility = if (show) View.VISIBLE else View.GONE
    }

}