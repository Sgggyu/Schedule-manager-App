package com.example.schedulemanager.ui.plan

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.example.schedulemanager.R
import com.example.schedulemanager.ScheduleManagerApplication.Companion.context
import com.example.schedulemanager.logic.model.ChineseConverter
import com.example.schedulemanager.logic.model.Plan
import com.example.schedulemanager.logic.model.TIMES
import com.example.schedulemanager.logic.model.getRender
import com.example.schedulemanager.logic.model.toChinese
import com.example.schedulemanager.ui.plan.PlanAdapter.Companion.PlanDiffCallback
import com.google.android.material.card.MaterialCardView

class PlanAdapter(val fragment: PlanFragment,) : ListAdapter<Plan, PlanAdapter.ViewHolder>(PlanDiffCallback) {
    var isEditMode = false // 是否处于编辑模式

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val planName: TextView = view.findViewById(R.id.tv_plan_name)
        val planInfo: TextView = view.findViewById(R.id.tv_plan_info)
        val card: MaterialCardView = view.findViewById(R.id.card_item_plan)
        val btnSelected: RadioButton = view.findViewById(R.id.rb_plan_selected)

    }

    companion object {
        object PlanDiffCallback : DiffUtil.ItemCallback<Plan>() {
            override fun areItemsTheSame(oldItem: Plan, newItem: Plan): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Plan, newItem: Plan): Boolean =
                oldItem == newItem


        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val viewHolder = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_plan, parent, false)
        )
        return viewHolder
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val plan = getItem(position)
        val triggerToChinese = mapOf(
            TIMES.EVERYDAY to "每天",
            TIMES.WEEKDAY to "工作日",
        )

        holder.planName.text =
            "${plan.name}(${getRender(plan.type).info})"
        holder.planInfo.text =
            "时间:${plan.triggerTime} |触发类型:${triggerToChinese[plan.triggerMode]}"

        if (plan.isEnable) {
            //假如计划启用，则卡片为实心色
            holder.card.setCardBackgroundColor(ContextCompat.getColor(context,getRender(plan.type).color) )
        }else{
            holder.card.setCardBackgroundColor(ContextCompat.getColor(context,R.color.colorSurface) )
        }

        //编辑模式
        if (isEditMode) {
            holder.btnSelected.visibility = View.VISIBLE
            holder.btnSelected.isChecked = fragment.viewModel.selectedList[position]
            holder.card.setOnClickListener {
                Log.v("test", "${fragment.viewModel.selectedList[position]}")
                fragment.viewModel.selectedList[position] = !holder.btnSelected.isChecked
                holder.btnSelected.isChecked = !holder.btnSelected.isChecked
            }
        }else{
            holder.btnSelected.visibility = View.INVISIBLE
            holder.card.setOnClickListener {
                plan.isEnable = !plan.isEnable
                when(plan.isEnable){
                    true -> holder.card.setCardBackgroundColor(ContextCompat.getColor(context,getRender(plan.type).color) )
                    false -> holder.card.setCardBackgroundColor(ContextCompat.getColor(context,R.color.colorSurface) )
                }
            }
        }

        holder.card.setOnLongClickListener {
            if(isEditMode == false){
                fragment.viewModel.isEdit.value = true
                fragment.viewModel.selectedList[position] = true
            }

            true
        }

    }



    fun enterEditMode(isEditMode: Boolean) {
        // 在这里实现切换编辑模式的逻辑
        // 例如，显示或隐藏删除按钮，启用或禁用拖拽等
        this.isEditMode = isEditMode
        if (!isEditMode) {
            fragment.viewModel.selectedList.fill(false) // 重置选中状态
        }
        notifyDataSetChanged() // 通知适配器刷新视图
    }

    fun deletePlan() {
        // 在这里实现删除计划的逻辑
        // 例如，从数据源中删除计划，并通知适配器更新
        // 视图
        fragment.viewModel.selectedList.fill(false)
    // 重置选中状态
    }

}