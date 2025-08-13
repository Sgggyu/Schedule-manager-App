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
import com.example.schedulemanager.ScheduleManagerApplication
import com.example.schedulemanager.databinding.FragmentEventBinding
import com.example.schedulemanager.logic.model.Event
import java.time.LocalDateTime

class EventFragment : Fragment() {
    private  var _binding: FragmentEventBinding?=null
    val binding get() = _binding!!
    val viewModel by lazy { ViewModelProvider(this).get(EventViewModel::class.java)}
    var currentTime = LocalDateTime.now()
    lateinit var adapter: EventAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.rvWeekEvent.layoutManager = LinearLayoutManager(ScheduleManagerApplication.context, LinearLayoutManager.HORIZONTAL,false)
        Log.v("test",viewModel.monthEvents.size.toString())
        adapter =  EventAdapter(viewModel.monthEvents, this)
        binding.rvWeekEvent.adapter = adapter

        viewModel.Events.observe(this, Observer({
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

        viewModel.currentTime.value = currentTime
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvWeekEvent)
    }

}