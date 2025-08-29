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
    var planList = MutableLiveData<Result<List<Plan>>>()
    var selectedList = MutableList<Boolean>(0){false}
    var plans = ArrayList<Plan>()

    fun insertPlan(plan: Plan){
        viewModelScope.launch {
            Repository.insertPlan(plan)
        }
    }

    fun deletePlans(plans: List<Plan>){
        viewModelScope.launch {
            Repository.deletePlans(plans.map { it.id })
        }
    }
    fun refreshPlans(){
        viewModelScope.launch {
            try {
                val plans = Repository.getAllPlans()
                planList.value = Result.success(plans)
            } catch (e: Exception) {
                planList.value = Result.failure(e)
            }
        }
    }
}