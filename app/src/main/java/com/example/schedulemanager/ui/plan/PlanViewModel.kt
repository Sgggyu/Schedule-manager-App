package com.example.schedulemanager.ui.plan

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schedulemanager.logic.Repository
import com.example.schedulemanager.logic.model.Plan
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PlanViewModel: ViewModel() {
    val isEdit = MutableLiveData<Boolean>(false)
    var planList = Repository.getAllPlans()
    var selectedList = MutableList<Boolean>(0){false}
    val plans = ArrayList<Plan>()

    fun insertPlan(plan: Plan){
        viewModelScope.launch {
            Repository.insertPlan(plan)
        }
    }


    fun refreshPlans(){
       planList = Repository.getAllPlans()
    }
}