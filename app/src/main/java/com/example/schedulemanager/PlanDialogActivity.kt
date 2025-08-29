package com.example.schedulemanager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.schedulemanager.databinding.ActivityPlanDialogBinding
import com.example.schedulemanager.logic.Repository
import com.example.schedulemanager.logic.model.Plan
import com.example.schedulemanager.logic.model.TIMES
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class PlanDialogActivity : AppCompatActivity() {
    lateinit var binding: ActivityPlanDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlanDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnPlanSave.isEnabled = false
        setListener()
        setSupportActionBar(binding.toolbar)
        binding.timePickerPlan.setIs24HourView(true)
        supportActionBar?.let{
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_close_plan)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
                return true
            }
            else -> true
        }
        return true
    }

    @SuppressLint("InflateParams")
    private fun setListener(){
        //输入部分
        val dialogView = layoutInflater.inflate(R.layout.dialog_set_plan_name, null, false)
        val editText : EditText = dialogView.findViewById(R.id.et_plan_input)
        val dialogPlanName = MaterialAlertDialogBuilder(this, R.style.CustomMaterialAlertDialog)
            .setTitle("规划名称 or 目标")
            .setView(dialogView)
            .setPositiveButton("确定") { dialog, which ->
                binding.tvPlanSetNameValue.text = editText.text
            }
            .setNegativeButton("取消",null)
            .create()
        dialogPlanName.setOnShowListener {
            editText.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }
        binding.inputPlanName.setOnClickListener {
            // 自动弹出键盘
            dialogPlanName.show()
        }
        //触发类型选择
        var triggerIndex = 0
        val dialogPlanTrigger = MaterialAlertDialogBuilder(this,R.style.CustomMaterialAlertDialog)
            .setTitle("触发类型")
            .setItems(arrayOf("每天","工作日")){ dialog, which ->
                val triggerToChinese = mapOf(
                    TIMES.EVERYDAY to "每天",
                    TIMES.WEEKDAY to "工作日",
                )
                triggerIndex = which
                binding.tvPlanSetTriggerValue.text = triggerToChinese[which]
            }
            .create()
        binding.inputPlanTrigger.setOnClickListener {
            dialogPlanTrigger.show()
        }
        //类型选择
        var typeIndex = -1
        val dialogPlanType = MaterialAlertDialogBuilder(this,R.style.CustomMaterialAlertDialog)
            .setTitle("规划类型")
            .setItems(arrayOf("睡觉","工作","锻炼","学习","吃饭","休闲","放松","其他")){ dialog, which ->
                val typeToChinese = mapOf(
                    0 to "睡觉",
                    1 to "工作",
                    2 to "锻炼",
                    3 to "学习",
                    4 to "吃饭",
                    5 to "休闲",
                    6 to "放松",
                    7 to "其他",
                )
                typeIndex = which
                binding.tvPlanSetTypeValue.text = typeToChinese[which]
            }
            .create()
        binding.inputPlanType.setOnClickListener {
            dialogPlanType.show()
        }

        //保存按钮状态检查，需要名字，类型，触发类型均不为空
        fun saveCheck() {
            binding.btnPlanSave.isEnabled = binding.tvPlanSetNameValue.text != ""
                    && binding.tvPlanSetTriggerValue.text != ""
                    && binding.tvPlanSetTypeValue.text != ""
        }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                saveCheck()
            }
        }
        binding.tvPlanSetNameValue.addTextChangedListener(watcher)
        binding.tvPlanSetTypeValue.addTextChangedListener(watcher)
        binding.tvPlanSetTriggerValue.addTextChangedListener(watcher)
        binding.btnPlanSave.setOnClickListener {
            val name = binding.tvPlanSetNameValue.text.toString()
            val type = binding.tvPlanSetTypeValue.text.toString()
            val trigger = binding.tvPlanSetTriggerValue.text.toString()
            val hour = binding.timePickerPlan.hour
            val minute = binding.timePickerPlan.minute
            val timeString = String.format("%02d:%02d", hour, minute)
            val intent = Intent().apply {
                putExtra("plan_name", name)
                putExtra("plan_type", typeIndex)
                putExtra("plan_trigger", triggerIndex)
                putExtra("plan_time", timeString)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }
}

