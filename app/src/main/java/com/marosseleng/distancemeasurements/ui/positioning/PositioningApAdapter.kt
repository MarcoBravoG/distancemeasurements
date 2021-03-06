/*
 * Copyright 2019 Maroš Šeleng
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.marosseleng.distancemeasurements.ui.positioning

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.marosseleng.distancemeasurements.R

class PositioningApAdapter(private val items: MutableList<ApInSpace> = mutableListOf()) :
    RecyclerView.Adapter<PositioningApAdapter.ViewHolder>() {

    fun getItems(): List<ApInSpace> = items

    fun addItem(item: ApInSpace) {
        items.add(0, item)
        notifyItemInserted(0)
    }

    fun clear() {
        val count = itemCount
        items.clear()
        notifyItemRangeRemoved(0, count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rtt_ap, parent, false)
        )
    }

    override fun getItemCount() = items.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.text.text = "${item.name ?: item.macAddress} @ [${item.position.first}, ${item.position.second}]"
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView as TextView
    }
}