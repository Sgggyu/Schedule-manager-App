package com.example.schedulemanager.ui.plan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.schedulemanager.R
import com.example.schedulemanager.databinding.FragmentEventBinding
import com.example.schedulemanager.databinding.FragmentPlanBinding

class PlanFragment : Fragment() {
    private  var _binding: FragmentPlanBinding?=null
    val binding get() = _binding!!
    val viewModel by lazy {
        ViewModelProvider(this)[PlanViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlanBinding.inflate(inflater,container, false)
        return binding.root
    }

}