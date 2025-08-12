package com.example.schedulemanager.ui.event

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.schedulemanager.R
import com.example.schedulemanager.databinding.FragmentEventBinding
import com.example.schedulemanager.logic.model.Event

class EventFragment : Fragment() {
    private  var _binding: FragmentEventBinding?=null
    val binding get() = _binding!!
    val viewModel = EventViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.eventsLiveData.observe(this, Observer({
            val events = it.getOrNull()
            if (events != null) {
                refreshUI(events)
            } else {
                Toast.makeText(activity,"未能查询到活动信息",Toast.LENGTH_SHORT).show()
                it.exceptionOrNull()?.printStackTrace()
            }
        }))
    }

    fun refreshUI(events: List<Event>) {

    }
}