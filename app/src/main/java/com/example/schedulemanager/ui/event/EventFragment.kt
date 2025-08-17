package com.example.schedulemanager.ui.event

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.schedulemanager.R
import com.example.schedulemanager.ScheduleManagerApplication
import com.example.schedulemanager.databinding.FragmentEventBinding
import com.example.schedulemanager.logic.model.Event
import java.time.LocalDateTime

class EventFragment : Fragment() {
    private  var _binding: FragmentEventBinding?=null
    val binding get() = _binding!!
    val viewModel by lazy { ViewModelProvider(this).get(EventViewModel::class.java)}
    lateinit var adapter: EventAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.currentTime.value = LocalDateTime.now()
        _binding = FragmentEventBinding.inflate(inflater,container,false)

        return binding.root
    }
    
    override fun onStart() {
        super.onStart()
        setListener()


        adapter =  EventAdapter(viewModel.monthEvents, this)
        binding.viewpagerEvent.adapter = adapter

        viewModel.Events.observe(this, Observer({
            val events = it.getOrNull()
            if (events!= null) {
                viewModel.monthEvents.clear()
                viewModel.monthEvents.addAll(events)
                adapter.notifyDataSetChanged()
                val position = Math.floor((viewModel.dayOfMonth-(8-viewModel.startTime.dayOfWeek.value))/7.0) + 1
                Log.v("test","position:$position") // 滚动到本周的第一天
            } else {
                Toast.makeText(context, "获取事件失败", Toast.LENGTH_SHORT).show()
                it.exceptionOrNull()?.printStackTrace()
            }

        }))



    }

    fun setListener(){
        binding.overlayView.setOnClickListener {
            binding.overlayView.visibility = View.GONE
        }

// 防止点击卡片本身时关闭
        binding.cvDetail.setOnClickListener {
            // 什么都不做，拦截点击事件
        }
    }

}