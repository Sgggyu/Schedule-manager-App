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
    suspend fun insertPlan(plan: Plan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlans(plans: List<Plan>)

    @Query("SELECT * FROM planTable")
    suspend fun getAllPlans(): List<Plan>

    @Query("DELETE FROM planTable WHERE id = :id")
    suspend fun deletePlan(id: Int)

    @Query("DELETE FROM planTable WHERE id IN (:ids)")
    suspend fun deletePlans(ids: List<Int>)

    @Query("DELETE FROM planTable")
    suspend fun clearAllPlans()
}