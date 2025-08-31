package com.example.schedulemanager.ui.plan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.schedulemanager.PlanDialogActivity
import com.example.schedulemanager.R
import com.example.schedulemanager.databinding.FragmentEventBinding
import com.example.schedulemanager.databinding.FragmentPlanBinding
import com.example.schedulemanager.logic.model.Plan
import com.example.schedulemanager.notification.AlarmHelper

class PlanFragment : Fragment() {
    private  var _binding: FragmentPlanBinding?=null
    val binding get() = _binding!!
    lateinit var adapter: PlanAdapter
    val viewModel by lazy {
        ViewModelProvider(this)[PlanViewModel::class.java]
    }
    val alarmHelper by lazy { AlarmHelper(requireContext()) }

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            val data = it.data
            val name = data?.getStringExtra("plan_name") ?: ""
            val typeIndex = data?.getIntExtra("plan_type",0) ?: 0
            val triggerIndex = data?.getIntExtra("plan_trigger",0) ?: -1
            val startTimeString = data?.getStringExtra("plan_start_time") ?: ""
            val endTimeString = data?.getStringExtra("plan_end_time") ?: ""
            val insertPlan = Plan(0,name,typeIndex,triggerIndex,startTimeString,endTimeString,false)
            viewModel.refreshAfterInsert(insertPlan)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

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
        //每次进入该fragment便会刷新
        viewModel.planList.observe(viewLifecycleOwner, Observer{
            Log.v("test","observe 触发")
            viewModel.plans.clear()
            viewModel.plans.addAll(viewModel.planList.value?.getOrNull()?: emptyList())
            Log.v("test","planList changed, new size is ${viewModel.plans.size}")
            viewModel.selectedList = MutableList(viewModel.plans.size){false}
            adapter.submitList(viewModel.plans)
        })

        viewModel.isEdit.observe(viewLifecycleOwner, Observer{
            if (it){
                adapter.enterEditMode(it)
                binding.btnDelete.setImageResource(R.drawable.ic_save)
            }
            else{
                adapter.enterEditMode(it)
                binding.btnDelete.setImageResource(R.drawable.ic_delete)
            }

        })
        viewModel.refreshPlans()
        //设置监听器
        setListener()
        return binding.root
    }

    override fun onStart() {
        super.onStart()

    }
    private fun setListener(){
        binding.btnDelete.setOnClickListener {
            if (viewModel.isEdit.value == true){
                //保存修改

                val newList = ArrayList<Plan>()
                val removeList = ArrayList<Plan>()
                for (i in 0 .. viewModel.selectedList.size-1){
                    //被选中的进入删除列表
                    when(viewModel.selectedList[i]){
                        true ->{
                            removeList.add(viewModel.plans[i])
                        }
                        false ->{
                            newList.add(viewModel.plans[i])
                        }
                    }
                }
                viewModel.plans.clear()
                viewModel.plans.addAll(newList)
                viewModel.selectedList = MutableList(viewModel.plans.size){false}
                adapter.submitList(viewModel.plans)
                viewModel.deletePlans(removeList)
                removeList.forEach {
                    alarmHelper.cancelAlarm(it)
                }
                viewModel.isEdit.value = false

            }else{
                //进入编辑模式
                viewModel.isEdit.value = true
            }
        }
        binding.fabPlan.setOnClickListener {
            val intent = Intent(this.context, PlanDialogActivity::class.java)
            launcher.launch(intent)
        }
    }

}