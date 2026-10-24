package com.example.thuchanhandroid.ui.employee

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.thuchanhandroid.R
import com.example.thuchanhandroid.data.local.entity.WorkLogEntity

class WorkLogAdapter(private var workLogs: List<WorkLogEntity>) :
    RecyclerView.Adapter<WorkLogAdapter.WorkLogViewHolder>() {

    class WorkLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvWorkDate)
        val tvCheckIn: TextView = itemView.findViewById(R.id.tvCheckIn)
        val tvCheckOut: TextView = itemView.findViewById(R.id.tvCheckOut)
        val tvTotalHours: TextView = itemView.findViewById(R.id.tvTotalHours)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkLogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_work_log, parent, false)
        return WorkLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkLogViewHolder, position: Int) {
        val log = workLogs[position]
        holder.tvDate.text = "Ngày: ${log.workDate}"
        holder.tvCheckIn.text = "Vào: ${log.checkInTime ?: "--:--"}"
        holder.tvCheckOut.text = "Ra: ${log.checkOutTime ?: "--:--"}"
        holder.tvTotalHours.text = "Số giờ: ${log.totalHours}h"
        holder.tvStatus.text = "Trạng thái: ${log.status}"
    }

    override fun getItemCount(): Int = workLogs.size

    fun updateData(newLogs: List<WorkLogEntity>) {
        workLogs = newLogs
        notifyDataSetChanged()
    }
}
