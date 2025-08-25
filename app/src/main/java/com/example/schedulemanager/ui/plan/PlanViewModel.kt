package com.example.schedulemanager.ui.plan

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schedulemanager.logic.Repository
import com.example.schedulemanager.logic.model.Plan
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PlanViewModel: ViewModel() {
    val isEnable = MutableLiveData<Boolean>()


    fun insertPlan(plan: Plan){
        viewModelScope.launch {
            Repository.insertPlan(plan)
        }
    }
}