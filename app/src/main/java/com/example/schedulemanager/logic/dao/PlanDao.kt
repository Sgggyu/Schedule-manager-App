package com.example.schedulemanager.logic.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.schedulemanager.logic.model.Plan

@Dao
interface PlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlan(plan: Plan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlans(plans: List<Plan>)

    @Query("SELECT * FROM Plan")
    fun getAllPlans(): List<Plan>

    @Query("DELETE FROM Plan WHERE id = :id")
    fun deletePlan(id: Int)

    @Query("DELETE FROM Plan")
    fun clearAllPlans()
}