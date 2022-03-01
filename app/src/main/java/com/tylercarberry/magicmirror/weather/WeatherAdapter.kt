package com.tylercarberry.magicmirror.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tylercarberry.magicmirror.BaseAdapter
import com.tylercarberry.magicmirror.R
import com.tylercarberry.magicmirror.Utils.DAY_NAME_PATTERN
import com.tylercarberry.magicmirror.databinding.WeatherItemBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class WeatherAdapter(
    items: List<Daily>
): BaseAdapter<Daily, WeatherItemBinding, WeatherAdapter.ViewHolder>(items) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        binding = WeatherItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val context = viewHolder.itemView.context
        val day = items[position]

        val formatter = SimpleDateFormat(DAY_NAME_PATTERN, Locale.US)
        val dateName = when (position) {
            0 -> context.getString(R.string.today)
            1 -> context.getString(R.string.tomorrow)
            else -> formatter.format(day.dt * 1000L)
        }

        with(binding) {
            weatherItemDate.text = dateName
            weatherItemDegreesHigh.text = context.getString(R.string.weather_degrees, day.temp.max.roundToInt())
            weatherItemDegreesLow.text = context.getString(R.string.weather_degrees, day.temp.min.roundToInt())
        }
    }

}