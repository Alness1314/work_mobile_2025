package com.susess.cv360.ui.receptions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.susess.cv360.R
import com.susess.cv360.databinding.FragmentReceptionBinding


class ReceptionFragment : Fragment() {
    private var _binding: FragmentReceptionBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReceptionBinding.inflate(inflater,container, false)
        return binding.root
    }

}