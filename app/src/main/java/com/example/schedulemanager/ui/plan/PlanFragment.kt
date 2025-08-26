package com.example.schedulemanager.ui.plan

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.schedulemanager.R
import com.example.schedulemanager.databinding.FragmentEventBinding
import com.example.schedulemanager.databinding.FragmentPlanBinding

class PlanFragment : Fragment() {
    private  var _binding: FragmentPlanBinding?=null
    val binding get() = _binding!!
    lateinit var adapter: PlanAdapter
    val viewModel by lazy {
        ViewModelProvider(this)[PlanViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.refreshPlans()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlanBinding.inflate(inflater,container, false)
        Log.v("test","viewModel.plans size is ${viewModel.plans.size}")

        adapter = PlanAdapter(this)
        binding.rvPlan.adapter = adapter
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvPlan.layoutManager = layoutManager

        viewModel.planList.observe(viewLifecycleOwner, Observer{
            viewModel.plans.clear()
            viewModel.plans.addAll(it.getOrNull() ?: emptyList())
            Log.v("test","planList changed, new size is ${viewModel.plans.size}")
            viewModel.selectedList = MutableList(viewModel.plans.size){false}
            adapter.submitList(viewModel.plans)
        })
        viewModel.refreshPlans()
        viewModel.isEdit.observe(viewLifecycleOwner, Observer{
            adapter.enterEditMode(it)
        })
        return binding.root
    }

}