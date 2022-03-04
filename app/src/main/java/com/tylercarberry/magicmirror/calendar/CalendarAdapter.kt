package com.tylercarberry.magicmirror.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import biweekly.component.VEvent
import com.tylercarberry.magicmirror.BaseAdapter
import com.tylercarberry.magicmirror.databinding.CalendarItemBinding
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(
    items: List<VEvent>
): BaseAdapter<VEvent, CalendarAdapter.ViewHolder>(items) {

    class ViewHolder(val binding: CalendarItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = CalendarItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val event = items[position]

        val formatter = SimpleDateFormat("MMMM d", Locale.US)
        with(viewHolder.binding) {
            eventDate.text = formatter.format(event.dateStart.value)
            eventName.text = event.summary.value
        }
    }

}