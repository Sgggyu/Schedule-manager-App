package com.example.schedulemanager.ui.event

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.schedulemanager.R
import com.example.schedulemanager.ScheduleManagerApplication.Companion.context
import com.example.schedulemanager.logic.Repository
import com.example.schedulemanager.logic.model.BagOfRender
import com.example.schedulemanager.logic.model.Event
import com.example.schedulemanager.logic.model.getRender
import com.example.schedulemanager.logic.model.toChinese
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
        val month = view.findViewById<TextView>(R.id.tv_month)
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


        holder.month.text = "${startOfWeek.monthValue}"
        // 渲染一周的日期和卡片
        for (i in 0 until 7) {
            val date = startOfWeek.plusDays(i.toLong())
            if (date.monthValue == currentTime.monthValue) {
                holder.dayList[i].text = date.dayOfMonth.toString()
                if (date.dayOfMonth == currentTime.dayOfMonth) {
                    holder.dayList[i].setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimary)) // 设置今天的日期背景色,蓝色
                }else{
                    holder.dayList[i].setBackgroundColor(ContextCompat.getColor(context,R.color.colorBackground)) // 设置今天的日期背景色,白色
                }
            } else {
                holder.dayList[i].text = ""
            }
            holder.containerList[i].removeAllViews()
            addEventsToDay(holder.containerList[i], monthList[date.dayOfMonth])
        }

    }

    override fun getItemCount(): Int {
        val currentTime = fragment.viewModel.currentTime.value!!
        val daysInMonth = currentTime.toLocalDate().lengthOfMonth()
        val firstDayOfWeek = currentTime.withDayOfMonth(1).dayOfWeek.value
        val endDayOfWeek = currentTime.withDayOfMonth(daysInMonth).dayOfWeek.value

        //一个月一供多少周

        return  (daysInMonth + firstDayOfWeek-8 - endDayOfWeek)/7 + 2
    }

    /**
     * 将事件渲染到指定的日期列容器
     * 并且添加监听器
     */
    private fun addEventsToDay(container: FrameLayout, events: List<Event>) {
        if (events.isEmpty())
            return
        container.post {
            //确定卡片高度
            val totalHeight = container.height
            val pxPerMinute = totalHeight / (24f * 60)
            //对每张卡片进行渲染
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
                val render = getRender(event.data.type)
                val title = card.findViewById<TextView>(R.id.tv_event_name)
                title.text = event.data.name
                val icon = card.findViewById<ImageView>(R.id.img_event_icon)
                icon.setImageResource(render.icon)
                //普通的setCardBackgroundColor会导致卡片的圆角失效
                //所以使用MaterialCardView的setCardBackgroundColor方法
                //注意：如果使用了MaterialCardView，需要在布局文件中使用MaterialCardView
                val cardView = card as MaterialCardView
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, render.color))

                val startTime = card.findViewById<TextView>(R.id.tv_startTime)
                val endTime = card.findViewById<TextView>(R.id.tv_endTime)
                val formatter =java.time.format.DateTimeFormatter.ofPattern("HH:mm")
                startTime.text = event.data.beginTime.format(formatter).toString()
                Log.v("test", "startTime: ${event.data.endTime.format(formatter)}")
                endTime.text = event.data.endTime.format(formatter).toString()
                container.addView(card)
                card.setOnClickListener {
                    // 设置数据
                    fragment.binding.tvName.text = event.data.name
                    fragment.binding.tvTimeRange.text = "星期${toChinese(event.data.day)}|${startTime.text} - ${endTime.text}" // 直接用现有 TextView
                    fragment.binding.etDescription.setText(event.data.description)
                    fragment.binding.overlayView.visibility = View.VISIBLE
                    fragment.binding.cvDetail.setCardBackgroundColor(ContextCompat.getColor(context, render.color))
                }

            }
        }
    }

}