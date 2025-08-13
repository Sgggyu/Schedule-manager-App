package com.example.schedulemanager.ui.event

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.schedulemanager.R
import com.example.schedulemanager.logic.model.Event
import java.time.LocalDate

class EventAdapter(val monthList: ArrayList<List<Event>>,val fragment: EventFragment):
    androidx.recyclerview.widget.RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayList = ArrayList<TextView>(
            listOf(
                view.findViewById(com.example.schedulemanager.R.id.tv_day1),
                view.findViewById(com.example.schedulemanager.R.id.tv_day2),
                view.findViewById(com.example.schedulemanager.R.id.tv_day3),
                view.findViewById(com.example.schedulemanager.R.id.tv_day4),
                view.findViewById(com.example.schedulemanager.R.id.tv_day5),
                view.findViewById(com.example.schedulemanager.R.id.tv_day6),
                view.findViewById(com.example.schedulemanager.R.id.tv_day7)
            )
        )
        // 用于存放每一天的卡片的容器
        val containerList = ArrayList<FrameLayout>(
            listOf(
                view.findViewById(com.example.schedulemanager.R.id.frame_day1) ,
                view.findViewById(com.example.schedulemanager.R.id.frame_day2) ,
                view.findViewById(com.example.schedulemanager.R.id.frame_day3) ,
                view.findViewById(com.example.schedulemanager.R.id.frame_day4) ,
                view.findViewById(com.example.schedulemanager.R.id.frame_day5) ,
                view.findViewById(com.example.schedulemanager.R.id.frame_day6) ,
                view.findViewById(com.example.schedulemanager.R.id.frame_day7)
            )
        )
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_week, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val currentTime = fragment.viewModel.currentTime.value!!
        val firstDayOfMonth = currentTime.withDayOfMonth(1)
        val firstMonday = firstDayOfMonth.minusDays(((firstDayOfMonth.dayOfWeek.value + 6) % 7).toLong())
        val startOfWeek = firstMonday.plusWeeks(position.toLong())



        // 渲染一周的日期和卡片
        for (i in 0 until 7) {
            val date = startOfWeek.plusDays(i.toLong())
            if (date.monthValue == currentTime.monthValue) {
                holder.dayList[i].text = date.dayOfMonth.toString()
            } else {
                holder.dayList[i].text = ""
            }
            holder.containerList[i].removeAllViews()
            addEventsToDay(holder.containerList[i], monthList[date.dayOfMonth])
        }

    }

    override fun getItemCount(): Int {
        val currentTime = fragment.viewModel.currentTime.value!!
        val firstDayOfWeek = currentTime.withDayOfMonth(1).dayOfWeek.value
        val daysInMonth = currentTime.toLocalDate().lengthOfMonth()
        return  (daysInMonth + firstDayOfWeek-8)/7+1
    }

    /**
     * 将事件渲染到指定的日期列容器中
     */
    private fun addEventsToDay(container: FrameLayout, events: List<Event>) {
        if (events.isEmpty())
            return
        container.post {
            val totalHeight = container.height
            val pxPerMinute = totalHeight / (24f * 60)

            for (event in events) {
                val card = LayoutInflater.from(container.context)
                    .inflate(R.layout.item_event_card, container, false)

                val params = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                val startMinutes = event.data.beginTime.hour * 60 + event.data.beginTime.minute
                val endMinutes = event.data.endTime.hour * 60 + event.data.endTime.minute

                params.topMargin = (startMinutes * pxPerMinute).toInt()
                params.height = ((endMinutes - startMinutes) * pxPerMinute).toInt()

                card.layoutParams = params

                // 设置事件标题
                val title = card.findViewById<TextView>(R.id.tv_event_name)
                title.text = event.data.name

                container.addView(card)
            }
        }
    }

}