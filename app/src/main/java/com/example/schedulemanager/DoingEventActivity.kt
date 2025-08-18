package com.example.schedulemanager

import android.os.Bundle
import android.os.SystemClock
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
import androidx.lifecycle.ViewModelProvider
import com.example.schedulemanager.databinding.ActivityDoingEventBinding
import com.example.schedulemanager.logic.Repository
import com.example.schedulemanager.logic.model.Event
import com.example.schedulemanager.logic.model.EventData
import com.example.schedulemanager.ui.doingEvent.DoingEventViewModel
import com.example.schedulemanager.ui.theme.ScheduleManagerTheme
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class DoingEventActivity : BaseActivity() {
    lateinit var binding: ActivityDoingEventBinding
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
        binding.tvDoingName.text = viewModel.eventName
        binding.tvDoingStartTime.text = viewModel.startTime.toString()
        binding.tvDoingType.text = viewModel.eventType
        binding.tvDoingGoal.text = viewModel.eventGoal
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

    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        return super.getOnBackInvokedDispatcher()
    }

    private fun setListener() {
        binding.fabAction.setOnClickListener {
            if (binding.fabAction.isExtended) {
                binding.fabAction.shrink()   // 已经展开 -> 收缩
            } else {
                binding.fabAction.extend()   // 已经收缩 -> 展开
            }
        }
        binding.fabAction.setOnLongClickListener {
            val dialog = MaterialAlertDialogBuilder(this)
                .setTitle("结束事件")
                .setMessage("是否结束当前事件？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定") { _: Any, _: Any ->
//                    val event = Event(EventData(
//                        viewModel.eventName,
//                        viewModel.startTime!!,
//                        LocalDateTime.now(),
//                        viewModel.startTime.dayOfWeek
//                        viewModel.typeId,
//
//                        )
//                    )
                    viewModel.eventName = ""
                    viewModel.startTime = null
                    viewModel.eventType = ""
                    viewModel.eventGoal = ""
                    Repository.clearEventInfo()

                    finish()
                }
                .create()
            dialog.show()
            true
        }
    }
}