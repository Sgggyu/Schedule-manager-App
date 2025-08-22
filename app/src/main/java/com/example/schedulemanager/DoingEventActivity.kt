package com.example.schedulemanager

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.util.Log
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.schedulemanager.databinding.ActivityDoingEventBinding
import com.example.schedulemanager.logic.Repository
import com.example.schedulemanager.logic.model.Event
import com.example.schedulemanager.logic.model.EventData
import com.example.schedulemanager.ui.doingEvent.DoingEventViewModel
import com.example.schedulemanager.ui.doingEvent.NotificationService
import com.example.schedulemanager.ui.theme.ScheduleManagerTheme
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DoingEventActivity : BaseActivity() {
    lateinit var binding: ActivityDoingEventBinding
    private var isEditMode = false  // 当前是否是编辑模式
    val viewModel by lazy {
        ViewModelProvider(this).get(DoingEventViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDoingEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //设置该页面无法用返回键退出
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Toast.makeText(this@DoingEventActivity, "请通过保存来结束当前任务", Toast.LENGTH_SHORT).show()
            }
        })
        val serviceIntent = Intent(this, NotificationService::class.java)
        startForegroundService(serviceIntent)
        //获取传递过来的数据
        if (viewModel.eventName == "") {
            viewModel.eventName = intent.getStringExtra("event_name") ?: ""
        }
        if (viewModel.startTime == null) {
            viewModel.startTime = Instant.ofEpochMilli(intent.getLongExtra("event_start_time",0)).atZone(
                ZoneId.systemDefault()).toLocalDateTime()
        }
        if (viewModel.eventType == "") {
            viewModel.eventType = intent.getStringExtra("event_type") ?: ""
        }
        if (viewModel.typeId == -1) {
            viewModel.typeId = intent.getIntExtra("event_index",-1)
        }
        if (viewModel.eventGoal == "") {
            viewModel.eventGoal = intent.getStringExtra("event_goal") ?: ""
        }
        val description = intent.getStringExtra("event_description")
        if (description != null) {
            viewModel.description = description
        }
        //初始化界面
        binding.tvDoingName.text = viewModel.eventName
        val formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss")
        binding.tvDoingStartTime.text = viewModel.startTime?.format(formatter)
        binding.tvDoingType.text = viewModel.eventType
        binding.tvDoingGoal.text = viewModel.eventGoal
        binding.etDoingSummary.text = Editable.Factory.getInstance().newEditable(viewModel.description)
        //由于base是基于手机开机时间来计算的
        //所以需要计算从开始时间到当前时间的毫秒数
        //这是计时器该显示的时间即从开始时间到当前时间的时间差
        val nowMillis = System.currentTimeMillis()
        val startMillis = viewModel.startTime!!.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val elapsedMillis = nowMillis - startMillis

        //base是设置一个时间锚，但是这个锚点是设置在系统开机时间上的
        //所以需要将当前系统开机时间减去时间差
        //计时器要显示的时间就变为从开始时间到当前时间的时间差
        binding.timer.base = SystemClock.elapsedRealtime() - elapsedMillis
        binding.timer.start()
        binding.fabAction.shrink()
        setListener()
    }
    override fun onDestroy() {
        super.onDestroy()
        Repository.saveDescription(viewModel.description)
    }
    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        return super.getOnBackInvokedDispatcher()
    }

    private fun setListener() {
        binding.textInputLayoutSummary.setEndIconOnClickListener {
            if (isEditMode) {
                // 保存逻辑
                val text = binding.etDoingSummary.text.toString()
                Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show()
                viewModel.description = text
                Repository.saveDescription(text)
                // 切换回查看模式
                binding.etDoingSummary.isEnabled = false
                binding.textInputLayoutSummary.endIconDrawable = ContextCompat.getDrawable(this, R.drawable.ic_edit)
                binding.textInputLayoutSummary.endIconContentDescription = "编辑"
            } else {
                // 切换到编辑模式
                binding.etDoingSummary.isEnabled = true
                binding.etDoingSummary.requestFocus()
                binding.textInputLayoutSummary.endIconDrawable = ContextCompat.getDrawable(this, R.drawable.ic_save)
                binding.textInputLayoutSummary.endIconContentDescription = "保存"
            }
            isEditMode = !isEditMode
        }

        binding.fabAction.setOnClickListener {
            if (binding.fabAction.isExtended) {
                binding.fabAction.shrink()   // 已经展开 -> 收缩
            } else {
                binding.fabAction.extend()   // 已经收缩 -> 展开
            }
        }

        binding.fabAction.setOnLongClickListener {
            val textTime = binding.timer.text.split(":").map { it.toInt() }
            val seconds = when(textTime.size) {
                3 -> textTime[0] * 3600L + textTime[1] * 60L + textTime[2]
                2-> textTime[0] * 60L + textTime[1]
                else -> 0L
            }
            if (seconds<900L || seconds>= 24*3600L){
                val dialog = MaterialAlertDialogBuilder(this,R.style.CustomMaterialAlertDialog)
                    .setTitle("时间错误")
                    .setMessage("当前任务时间不合法，必须大于15分钟且小于24小时,此次任务不记录,确定要结束吗？")
                    .setNegativeButton ("取消",null)
                    .setPositiveButton("确定"){
                            _: Any, _: Any ->
                        viewModel.eventName = ""
                        viewModel.startTime = null
                        viewModel.eventType = ""
                        viewModel.eventGoal = ""
                        viewModel.description = ""
                        Repository.clearEventInfo()
                        finish()
                    }
                dialog.show()
            }else {
                // 如果时间合法，弹出结束任务对话框
                val dialog = MaterialAlertDialogBuilder(this,R.style.CustomMaterialAlertDialog)
                    .setTitle("结束任务")
                    .setMessage("是否结束当前任务？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定") { _: Any, _: Any ->

                        val event = Event(0,EventData(
                            viewModel.eventName,
                            viewModel.startTime!!,
                            LocalDateTime.now(),
                            viewModel.startTime!!.dayOfWeek.value,
                            viewModel.typeId,
                            viewModel.description
                        )
                        )
                        lifecycleScope.launch {
                            Repository.insertEvent(event)
                        }
                        viewModel.eventName = ""
                        viewModel.startTime = null
                        viewModel.eventType = ""
                        viewModel.eventGoal = ""
                        viewModel.description = ""
                        Repository.clearEventInfo()
                        finish()
                    }
                dialog.show()
            }
            true
        }

    }
}