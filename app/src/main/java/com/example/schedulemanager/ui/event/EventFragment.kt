package com.example.schedulemanager.ui.event


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.compose.material3.RangeSlider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.schedulemanager.DoingEventActivity
import com.example.schedulemanager.MainActivity
import com.example.schedulemanager.R
import com.example.schedulemanager.ScheduleManagerApplication
import com.example.schedulemanager.databinding.FragmentEventBinding
import com.example.schedulemanager.logic.Repository
import com.example.schedulemanager.logic.model.Event
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.floor

class EventFragment : Fragment() {
    private  var _binding: FragmentEventBinding?=null
    val binding get() = _binding!!
    val viewModel by lazy { ViewModelProvider(this)[EventViewModel::class.java] }
    lateinit var adapter: EventAdapter
//    val activity = requireActivity() as MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventBinding.inflate(inflater,container,false)
        viewModel.currentTime.value = LocalDateTime.now()
        binding.tvDatetime.text = "${viewModel.yearValue}年${viewModel.monthValue}月"
        setListener()
        binding.viewpagerEvent.offscreenPageLimit = viewModel.monthEvents.size
        adapter =  EventAdapter(viewModel.monthEvents, this)
        binding.viewpagerEvent.adapter = adapter

        //如果当前已经在进行活动，直接启动DoingEventActivity

        viewModel.Events.observe(viewLifecycleOwner, Observer({
            val events = it.getOrNull()

            if (events!= null) {
                viewModel.monthEvents.clear()
                viewModel.monthEvents.addAll(events)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(context, "获取事件失败", Toast.LENGTH_SHORT).show()
                it.exceptionOrNull()?.printStackTrace()
            }
        }))
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (Repository.isEventInfoSaved()){
            val intent = Intent(activity, DoingEventActivity::class.java)
            val eventMap = Repository.getSavedEventInfo()
            intent.putExtra("event_name", eventMap["eventName"] as String )
            intent.putExtra("event_type", eventMap["eventType"] as String)
            intent.putExtra("event_index", eventMap["typeId"] as Int)
            intent.putExtra("event_start_time", eventMap["startTime"] as Long)
            intent.putExtra("event_goal", eventMap["eventGoal"] as String)
            intent.putExtra("event_description", Repository.getEventDescription())
            startActivity(intent)
        }
        if (Repository.isPlanSaved()){
            val dialog = MaterialAlertDialogBuilder(requireContext(),R.style.CustomMaterialAlertDialog )
                .setTitle("是否开始规划任务？")
                .setMessage("进行${Repository.getSavedPlanInfo()["planName"]}")
                .setPositiveButton("发起活动") { _, _ ->
                    val planMap = Repository.getSavedPlanInfo()
                    val planName = planMap["planName"] as String
                    val planType = planMap["planType"] as Int
                    val type = "规划任务"
                    val duration = planMap["goalMinute"] as Int
                    val startTimeMillis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    val intent = Intent(activity, DoingEventActivity::class.java)
                    intent.putExtra("event_name", planName)
                    intent.putExtra("event_type", type)
                    intent.putExtra("event_index", planType)
                    intent.putExtra("event_start_time", startTimeMillis)
                    intent.putExtra("event_goal", "${duration}分钟")
                    intent.putExtra("event_duration", duration)
                    startActivity(intent)
                    Repository.saveEventInfo(planName,type,planType,startTimeMillis,"坚持${duration}分钟")
                    Repository.clearPlanInfo()
                }
                .setNegativeButton ("取消"){_, _ ->
                    Repository.clearPlanInfo()
                }
                .create()
            dialog.show()
        }
    }

    fun setListener(){
        //下拉刷新监听器
        binding.btnRefresh.setOnClickListener {
            refreshEvents()
        }
        //日期监听器
        binding.linearSetDatetime.setOnClickListener {
            val datePickerDialog = DatePickerDialog(requireContext(),null,viewModel.yearValue,viewModel.monthValue-1,viewModel.dayOfMonth)
            datePickerDialog.show()
            datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                // 确认年月日
                val year = datePickerDialog.getDatePicker().getYear();
                val monthOfYear = datePickerDialog.getDatePicker().getMonth()+1 ;
                val dayOfMonth = datePickerDialog.getDatePicker().getDayOfMonth();
                viewModel.currentTime.value = LocalDateTime.of(year,monthOfYear,dayOfMonth,0,0)

                // 关闭dialog
                datePickerDialog.dismiss()
                binding.tvDatetime.text = "${viewModel.yearValue}年${viewModel.monthValue}月"
            }
        }

        //悬浮按钮监听器
        binding.fab.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_cast_event, null)
            val editTextInput = dialogView.findViewById<android.widget.EditText>(R.id.editText_input)
            val editTextDropdown = dialogView.findViewById<android.widget.AutoCompleteTextView>(R.id.editText_dropdown)
            val editTextGoal = dialogView.findViewById<android.widget.EditText>(R.id.editText_goal)
            val items = listOf("睡觉", "工作", "锻炼", "学习", "吃饭", "休闲", "放松","其他")
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
            editTextDropdown.setAdapter(adapter)
            var selectedIndex = -1 // 用于记录选中的下拉框索引
            editTextDropdown.setOnItemClickListener { _, _, position, _ ->
                selectedIndex = position
            }
            //处理对话框逻辑
            //先构建对话框
            val dialog = MaterialAlertDialogBuilder(requireContext(),R.style.CustomMaterialAlertDialog )
                        .setView(dialogView)
                        .setPositiveButton("发起活动",null)
                        .setNegativeButton ("取消",null)
                        .create()
            // 监听对话框显示事件
            //禁用确定按钮，直到输入有效
            dialog.setOnShowListener {
                val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton.isEnabled = false // 初始时禁用按钮
            }
            // 显示对话框，为了后续取得按钮实例
            dialog.show()
            // 获取对话框中的正面按钮，来设置它的可用状态
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            // 正面按钮可用状态的验证逻辑
            fun validateInput() {
                val inputText = editTextInput.text.toString().trim()
                positiveButton.isEnabled =  inputText.isNotEmpty() && selectedIndex != -1
            }
            editTextInput.addTextChangedListener(object : TextWatcher{
                override fun afterTextChanged(s: android.text.Editable?) {
                    validateInput()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            // 下拉框选择监听
            editTextDropdown.setOnItemClickListener { _, _, position, _ ->
                selectedIndex = position
                validateInput()
            }

            // 点击确定逻辑
            positiveButton.setOnClickListener {
                val input = editTextInput.text.toString().trim()
                val index = selectedIndex
                val goal = editTextGoal.text.toString().trim()
                val selectedText = items[index]
                val startTimeMillis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val intent = Intent(activity, DoingEventActivity::class.java)
                intent.putExtra("event_name", input)
                intent.putExtra("event_type", selectedText)
                intent.putExtra("event_index", index)
                intent.putExtra("event_start_time", startTimeMillis)
                intent.putExtra("event_goal", goal)
                startActivity(intent)
                Repository.saveEventInfo(input,selectedText,index,startTimeMillis,goal)
                dialog.dismiss()
            }
        }

        //点击卡片以外的背景可以让卡片消失
        binding.overlayView.setOnClickListener {
            binding.overlayView.visibility = View.GONE
        }

        // 防止点击卡片本身时关闭
        binding.cvDetail.setOnClickListener {
            // 什么都不做，拦截点击事件
        }

    }

    private fun refreshEvents() {
        // 刷新事件列表
        viewModel.currentTime.value = viewModel.currentTime.value
    }

}