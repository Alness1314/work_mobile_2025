package com.susess.cv360.ui.receptions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.susess.cv360.R
import com.susess.cv360.adapters.ModuleAdapter
import com.susess.cv360.adapters.ReceptionAdapter
import com.susess.cv360.databinding.FragmentReceptionBinding
import com.susess.cv360.ui.dashboard.DashboardViewModel
import com.susess.cv360.ui.events.EventsViewModel
import com.susess.cv360.ui.pickers.DatetimePickers
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDate

@AndroidEntryPoint
class ReceptionFragment : Fragment() {
    private var _binding: FragmentReceptionBinding? = null
    private val binding get() = _binding!!

    private val receptionViewModel: ReceptionViewModel by viewModels()
    private lateinit var adapter: ReceptionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReceptionBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecycler()
        setupObservers()
        setupListeners()
        initValues()
    }

    fun setupRecycler(){
        adapter = ReceptionAdapter{}
        binding.recyclerReceptions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerReceptions.adapter = adapter
    }

    fun setupObservers(){
        receptionViewModel.receptions.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }

        receptionViewModel.uiState.observe(viewLifecycleOwner){ state ->
            when (state) {
                is ReceptionViewModel.UiState.Loading -> {
                    showLoading(true)
                }
                is ReceptionViewModel.UiState.Success -> {
                    showLoading(false)
                }
                is ReceptionViewModel.UiState.Error -> {
                   showLoading(false)
                    Snackbar.make(binding.root, "Error: ${state.message}", Snackbar.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }

        receptionViewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is ReceptionViewModel.NavigationEventReceptions.ToDashboard -> {
                    findNavController().navigate(R.id.action_navigation_reception_to_navigation_dashboard)
                }
            }
        }

    }

    fun initValues(){
        val date = LocalDate.now().toString()
        binding.inputDateStart.setText(date)
        binding.inputDateEnd.setText(date)
        receptionViewModel.loadReceptions(startDate = date, endDate = date)
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
            receptionViewModel.loadReceptions(startDate = startDate, endDate = endDate)
        }
    }

    private fun showLoading(show: Boolean) {
        binding.layoutProgressReceptions.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}