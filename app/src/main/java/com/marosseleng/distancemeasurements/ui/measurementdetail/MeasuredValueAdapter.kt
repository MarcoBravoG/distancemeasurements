package com.marosseleng.distancemeasurements.ui.measurementdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.marosseleng.distancemeasurements.R
import com.marosseleng.distancemeasurements.data.MeasuredValue
import kotlinx.android.synthetic.main.item_measured_value.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Maroš Šeleng
 */
class MeasuredValueAdapter : RecyclerView.Adapter<MeasuredValueAdapter.ViewHolder>() {

    var items: List<MeasuredValue> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_measured_value, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        with(holder) {
            title.text = item.id.toString()
            val formatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSS", Locale.US)
            time.text = formatter.format(Date(item.timestamp))
            distance.text = item.measuredValue.toString()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.title
        val time: TextView = itemView.time
        val distance: TextView = itemView.distance
    }
}